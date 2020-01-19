package com.nike.s3copier;

import com.nike.s3copier.logger.CopyLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class S3copierApplication {

	private static Logger LOGGER = CopyLogger.PRIMARY_LOGGER;

	public static void main(String[] args) {
		SpringApplication.run(S3copierApplication.class, args);
	}

}
