package com.danielptm.s3copier.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.danielptm.s3copier.logger.CopyLogger;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

public class Client {

    private Logger LOGGER = CopyLogger.PRIMARY_LOGGER;
    private AmazonS3 getS3;
    private AmazonS3 writeS3;

    public Client() {
        getS3 = AmazonS3ClientBuilder.defaultClient();
        writeS3 = AmazonS3ClientBuilder.defaultClient();
    }

    public Client(Map<String, String> awsCredentials) {
        BasicSessionCredentials readCreds = new BasicSessionCredentials(
                awsCredentials.get("originKey"),
                awsCredentials.get("originSecretKey"),
                awsCredentials.get("originToken"));

        getS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(readCreds))
                .build();

        BasicSessionCredentials writeCreds = new BasicSessionCredentials(
                awsCredentials.get("destinationKey"),
                awsCredentials.get("destinationSecretKey"),
                awsCredentials.get("destinationToken"));

        writeS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(writeCreds))
                .build();
    }

    public boolean writeFile(String bucket, String fileName, String fileContents) {
        boolean didWriteSuccessfuly = false;
        try {
            writeS3.putObject(bucket, fileName, fileContents);
            didWriteSuccessfuly = true;
        } catch(Exception e) {
            LOGGER.error(String.format("There was a problem when writing to S3 with fileName: %s", fileName), e);
            didWriteSuccessfuly = false;
        }
        return didWriteSuccessfuly;
    }

    public String getFile(String bucket, String key) {
        StringWriter sw = null;
        try (S3Object s3Object = getS3.getObject(bucket, key); InputStream inputStream = s3Object.getObjectContent()) {
            sw = new StringWriter();
            IOUtils.copy(inputStream, sw, Charset.forName("UTF-8"));
        } catch (Exception e) {
            LOGGER.info(String.format("There was a problem when getting a file from s3 with file: {}", key), e);
        }
        return sw.toString();
    }

    public ListObjectsV2Result getAllObjetsInS3Location(String bucket, String prefix) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
        listObjectsV2Request.setBucketName(bucket);
        listObjectsV2Request.setPrefix(prefix);
        ListObjectsV2Result listObjectsV2Result = this.getS3.listObjectsV2(listObjectsV2Request);
        return listObjectsV2Result;
    }
}