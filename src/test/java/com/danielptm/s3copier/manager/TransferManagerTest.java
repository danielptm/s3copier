package com.danielptm.s3copier.manager;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.danielptm.s3copier.s3.Client;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferManagerTest {

    String prefix = "abc/def/";
    String bucket = "nike-commerce-test-app-internal";
    List<S3ObjectSummary> s3ObjectSummaryList = new ArrayList<>();
    String absolutePath;
    TransferManager transferManager;

    @BeforeEach
    public void setup() throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("config.json");
        File file = Paths.get(res.toURI()).toFile();
        absolutePath = file.getAbsolutePath();

        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey(prefix + "hi.txt");
        S3ObjectSummary s3ObjectSummary2 = new S3ObjectSummary();
        s3ObjectSummary2.setKey(prefix + "hij/hi.txt");
        S3ObjectSummary s3ObjectSummary3 = new S3ObjectSummary();
        s3ObjectSummary3.setKey(prefix);
        s3ObjectSummaryList.add(s3ObjectSummary);
        s3ObjectSummaryList.add(s3ObjectSummary2);
        s3ObjectSummaryList.add(s3ObjectSummary3);
        transferManager = new TransferManager.Builder(absolutePath).build();
    }

    /**
     * This is not a typical unit test. Use this to smoke test AWS services.
     */
    @Ignore
    public void testing() {
        String key = "";
        String secretKey = "";
        String token = "";

        Map<String, String> awsCredentials = new HashMap<>();
        awsCredentials.put("originKey", key);
        awsCredentials.put("originSecretKey", secretKey);
        awsCredentials.put("originToken", token);


        awsCredentials.put("destinationKey", key);
        awsCredentials.put("destinationSecret", secretKey);
        awsCredentials.put("destinationToken", token);

        Client client = new Client(awsCredentials);
        ListObjectsV2Result listObjectsV2Result = client.getAllObjetsInS3Location("nike-commerce-test-app-internal", "applications/price-jei/additional-product-attributes/");
        transferManager.getFilesRecursively("applications/price-jei/additional-product-attributes/", listObjectsV2Result);

    }

    @Test
    public void testEndOfPrefix() {
        String desiredLevel = "abc/def/hij/";
        String withoutSubFolder = "abc/def/hij/hi.txt";
        String withSubFolder = "abc/def/hij/lmn/hi.txt";
        Client client = new Client();

        Assertions.assertFalse(
                transferManager.isAtLevel(desiredLevel.split("/").length,
                        withSubFolder)
        );

        Assertions.assertTrue(
                transferManager.isAtLevel(desiredLevel.split("/").length, withoutSubFolder)
        );
    }

    @Test
    public void testGetFilesRecursively() {
        ListObjectsV2Result listObjectsV2Result = Mockito.mock(ListObjectsV2Result.class);
        Mockito.when(listObjectsV2Result.getObjectSummaries())
                .thenReturn(s3ObjectSummaryList);
        Client client = new Client();
        List<String> items = transferManager.getFilesRecursively(prefix, listObjectsV2Result);
        Assertions.assertEquals(2, items.size());
    }

    @Test
    public void testGetFilesOnlyAtLevel() {
        ListObjectsV2Result listObjectsV2Result = Mockito.mock(ListObjectsV2Result.class);
        Mockito.when(listObjectsV2Result.getObjectSummaries())
                .thenReturn(s3ObjectSummaryList);
        List<String> items = transferManager.getFilesOnlyAtLevel(prefix, listObjectsV2Result);
        Assertions.assertEquals(1, items.size());
    }

    @Test
    public void testCreateFileNameToWrite() {
        TransferManager transferManager = new TransferManager.Builder(this.absolutePath).build();
        String fileName = "abc/def/hi.txt";
        String result = transferManager.createFileNameToWrite(fileName);
        Assertions.assertEquals("hi.txt", result);
    }

}
