package com.danielptm.s3copier.config;

import com.danielptm.s3copier.logger.CopyLogger;
import com.danielptm.s3copier.model.CopyConfig;
import com.danielptm.s3copier.model.CopyType;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
public class CopyConfigFactory {

    private static Logger LOGGER = CopyLogger.PRIMARY_LOGGER;

    /**
     * Provide the absolute path for the config.json file and get back a validated CopyConfig object.
     * @param absolutePath
     * @return
     */
    public static CopyConfig getInstance(String absolutePath) {
        CopyConfig copyConfig = loadCopyConfig(absolutePath);
        boolean result = validateConfig(copyConfig);
        if (!result) {
            throw new IllegalArgumentException("The configuration file provided was not valid. Please view the README.md file to learn about how to structure a valid config.json file.");
        }
        return copyConfig;
    }

    /**
     * Provide the absolute path for a file on the linux system.
     * @param absolutePath
     */
    static CopyConfig loadCopyConfig(String absolutePath) {
        Path path = Paths.get(absolutePath);
        StringWriter sw = null;
        try (InputStream inputStream = Files.newInputStream(path);) {
            sw = new StringWriter();
            IOUtils.copy(inputStream, sw, Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOGGER.error("There was a problem reading the configuration file: ", e);
        }
        CopyConfig copyConfig = new Gson().fromJson(sw.toString(), CopyConfig.class);
        return copyConfig;
    }

    static boolean validateConfig(CopyConfig copyConfig) {
        boolean valid = true;
        if (copyConfig.getS3OriginBucket() == null
                || copyConfig.getS3DestinationBucket() == null
                || copyConfig.getPrefixOfReadLocation() == null
                || copyConfig.getPrefixOfWriteLocation() == null
                || copyConfig.getCopyType() == null
        ) {
            valid = false;
        }
        Map<String, String> awsCredentials = copyConfig.getAwsCredentials();
        if (awsCredentials != null)  {
            if (!(awsCredentials.containsKey("originKey"))
            || !(awsCredentials.containsKey("originSecretKey"))
            || !(awsCredentials.containsKey("originToken"))
            || !(awsCredentials.containsKey("destinationKey"))
            || !(awsCredentials.containsKey("destinationSecretKey"))
            || !(awsCredentials.containsKey("destinationToken"))) {
                valid = false;
            }
        }
        List<String> fileList =  copyConfig.getFileList();

        // If the fileList exists, but COPY_SPECIFIC_FILES is not marked then throw an error because you can't include a list of files to copy but then say you dont want them to be copied.
        if (fileList != null && fileList.size() > 0 && copyConfig.getCopyType() != CopyType.COPY_SPECIFIC_FILES) {
            LOGGER.warn("The copyType does not correspond to filesList correctly.");
            valid = false;
        }
        // If COPY_SPECIFIC_FILES is marked, but there is no list or an empty list, throw an error because there have to be files in order to copy specific files.
        if (copyConfig.getCopyType() == CopyType.COPY_SPECIFIC_FILES && (fileList == null || fileList.size() == 0)) {
            LOGGER.warn("The copyType does not correspond to filesList correctly.");
            valid = false;
        }
        return valid;
    }
}
