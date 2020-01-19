package com.nike.s3copier.config;

import com.nike.s3copier.model.CopyConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class CopyConfigFactoryTest {


    @Test
    public void testLoadCopyConfig() throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("config.json");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();
        CopyConfig copyConfig = CopyConfigFactory.getInstance(absolutePath);
        Assertions.assertEquals(copyConfig.getS3OriginBucket(), "nike-commerce-test-app-internal");
    }

    @Test
    public void testinThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            URL res2 = getClass().getClassLoader().getResource("badConfig.json");
            File file = Paths.get(res2.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();
            CopyConfigFactory.getInstance(absolutePath);
        });
    }
}
