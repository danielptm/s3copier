package com.danielptm.s3copier.manager;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.danielptm.s3copier.logger.CopyLogger;
import com.danielptm.s3copier.config.CopyConfigFactory;
import com.danielptm.s3copier.model.CopyConfig;
import com.danielptm.s3copier.model.CopyType;
import com.danielptm.s3copier.s3.Client;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class TransferManager {

    private static Logger PRIMARY_LOGGER = CopyLogger.PRIMARY_LOGGER;
    private static Logger  FILES_COMPLETED_LOGGER = CopyLogger.FILES_COMPLETED_LOGGER;

    private List<String> filesToGet;
    private CopyConfig copyConfig;
    private Client client;

    public TransferManager(Builder builder) {
        this.filesToGet = builder.filesToGet;
        this.copyConfig = builder.copyConfig;
        this.client = builder.client;
    }

    public static class Builder {
        private List<String> filesToGet;
        private CopyConfig copyConfig;
        private Client client;
        private String absolutePath;

        public Builder(String absolutePath) {
            this.absolutePath = absolutePath;
            copyConfig = CopyConfigFactory.getInstance(this.absolutePath);
        }

        public Builder withClient() {
            if (copyConfig.getAwsCredentials() == null) {
                client = new Client();
            } else {
                client = new Client(this.copyConfig.getAwsCredentials());
            }
            return this;
        }
        public Builder withFiles() {
            //If CopyType.COPY_FROM_LEVEL
            //Get all files (not folders) from that level.
            //Add them to filesToGet
            if (copyConfig.getCopyType() == CopyType.COPY_SPECIFIC_FILES) {
                filesToGet = copyConfig.getFileList();
            } else {
                ListObjectsV2Result listObjectsV2Result = client
                        .getAllObjetsInS3Location(copyConfig.getS3OriginBucket(), copyConfig.getPrefixOfReadLocation());
                String x = null;
                //If CopyType.COPY_RECURSIVELY
                //Get all files at that level, and from all levels below it.
                if (copyConfig.getCopyType() == CopyType.COPY_RECURSIVELY) {
                    filesToGet = getFilesRecursively(copyConfig.getPrefixOfReadLocation(), listObjectsV2Result);
                }
                //If CopyType.COPY_SPECIFIC_FILES
                //Just get the files specified copyConfig.filesList;
                if (copyConfig.getCopyType() == CopyType.COPY_FROM_LEVEL) {
                    filesToGet = getFilesOnlyAtLevel(copyConfig.getPrefixOfReadLocation(), listObjectsV2Result);
                }
            }
            return this;
        }

        public TransferManager build() {
            return new TransferManager(this);
        }
    }

    static List<String> getFilesRecursively(String prefix, ListObjectsV2Result result) {
        List<String> results = result.getObjectSummaries()
                .stream()
                .filter(summary -> !summary.getKey().equals(prefix))
                .map(summary -> summary.getKey())
                .collect(Collectors.toList());
        return results;
    }

    static List<String> getFilesOnlyAtLevel(String prefix, ListObjectsV2Result result) {
        int levels = prefix.split("/").length;
        List<String> results = result.getObjectSummaries()
                .stream()
                .filter(summary -> !summary.getKey().equals(prefix))
                .map(summary -> summary.getKey())
                .filter(key -> isAtLevel(levels, key))
                .collect(Collectors.toList());
        return results;
    }

    /**
     * Checks to make sure the filename is at the right level.
     * Note: The filename is the prefix + the actual name of the file.
     * @param levels
     * @param fileName is the s3 prefix and the name of the file. Example. abc/def/hi.txt
     * @return true if the file is at the correct level false otherwise.
     */
    static boolean isAtLevel(int levels, String fileName) {
        return (fileName.split("/").length - 1) == levels;
    }

    String createFileNameToWrite(String readFileName) {
        int lastDashIndex = readFileName.lastIndexOf("/") + 1;
        int stringLength = readFileName.length();
        return readFileName.substring(lastDashIndex, stringLength);
    }

    public void runTransfer() {
        if (filesToGet != null) {
            for (String fileName : filesToGet) {
                String contents = client.getFile(copyConfig.getS3OriginBucket(), fileName);
                String newFile = copyConfig.getPrefixOfWriteLocation() + createFileNameToWrite(fileName);
                boolean successfullyWritten = client.writeFile(copyConfig.getS3OriginBucket(), newFile, contents);
                if (successfullyWritten) {
                    FILES_COMPLETED_LOGGER.info(String.format("File written from: %s to %s", fileName, newFile));
                } else {
                    PRIMARY_LOGGER.warn(String.format("File failed writing from: %s to: %s", fileName, newFile));
                }
            }
            PRIMARY_LOGGER.info("Transfer completed...");
        } else {
            PRIMARY_LOGGER.warn("File list was not loaded. Make sure to builder the TransferManager withClient() and withFiles() before runTransfer()");
        }
    }
}
