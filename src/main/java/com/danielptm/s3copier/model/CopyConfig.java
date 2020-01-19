package com.danielptm.s3copier.model;

import java.util.List;
import java.util.Map;

public class CopyConfig {
    private final String s3OriginBucket;
    private final String s3DestinationBucket;
    private final String prefixOfReadLocation;
    private final String prefixOfWriteLocation;
    private final CopyType copyType;
    private final Map<String, String> awsCredentials;
    private final List<String> fileList;

    public CopyConfig(String s3OriginBucket, String s3DestinationBucket, String prefixOfReadLocation, String prefixOfWriteLocation, CopyType copyType, Map<String, String> awsCredentials, List<String> fileList) {
        this.s3OriginBucket = s3OriginBucket;
        this.s3DestinationBucket = s3DestinationBucket;
        this.prefixOfReadLocation = prefixOfReadLocation;
        this.prefixOfWriteLocation = prefixOfWriteLocation;
        this.copyType = copyType;
        this.awsCredentials = awsCredentials;
        this.fileList = fileList;
    }

    public String getS3OriginBucket() {
        return s3OriginBucket;
    }

    public String getS3DestinationBucket() {
        return s3DestinationBucket;
    }

    public String getPrefixOfReadLocation() {
        return prefixOfReadLocation;
    }

    public String getPrefixOfWriteLocation() {
        return prefixOfWriteLocation;
    }

    public CopyType getCopyType() {
        return copyType;
    }

    public Map<String, String> getAwsCredentials() {
        return awsCredentials;
    }

    public List<String> getFileList() {
        return fileList;
    }
}
