package com.nike.s3copier;

import com.nike.s3copier.logger.CopyLogger;
import com.nike.s3copier.manager.TransferManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Run implements CommandLineRunner {
    Logger LOGGER = CopyLogger.PRIMARY_LOGGER;
    @Override
    public void run(String... args) throws Exception {
        if (validateArgs(args)) {
            LOGGER.info("Starting transfer...");
            TransferManager transferManager = new TransferManager.Builder(args[1])
                    .withClient()
                    .withFiles()
                    .build();
            transferManager.runTransfer();
        }
    }

    boolean validateArgs(String...args) {
        if (!args[0].equals("-h") && !args[0].equals("-p") && !args[0].equals("-example")) {
            printInstructions();
            return false;
        } else if (args[0].equals("-p")) {
            if (args.length < 2) {
                System.out.println("You must supply an absolute file path to the config.json file.");
                return false;
            } else {
                return true;
            }
        } else if (args[0].equals("-example")) {
            printConfigExample();
            return false;
        } else if (args[0].equals("-h")) {
            printInstructions();
            return false;
        }
        printInstructions();
        return false;
    }

    void printConfigExample() {
        System.out.println("An example of a valid config.json file:");
        System.out.println("");
        System.out.println("{\n" +
                "  \"s3OriginBucket\":\"nike-commerce-test-app-internal\",\n" +
                "  \"s3DestinationBucket\":\"nike-commerce-test-app-internal\",\n" +
                "  \"prefixOfReadLocation\":\"test/read/prefix/\",\n" +
                "  \"prefixOfWriteLocation\":\"test/write/prefix/\",\n" +
                "  \"copyType\":\"COPY_SPECIFIC_FILES\",\n" +
                "  \"awsCredentials\" : {\n" +
                "    \"originKey\": \"testValue\",\n" +
                "    \"originSecret\": \"testValue\",\n" +
                "    \"originToken\":\"testValue\",\n" +
                "    \"destinationKey\":\"testValue\",\n" +
                "    \"destinationSecret\":\"testvalue\",\n" +
                "    \"destinationToken\":\"testvalue\"\n" +
                "  },\n" +
                "  \"fileList/folder/hi.txt\": [\"testFile1/folder/hi.txt\", \"testFile2/folder/hi.txt\"]\n" +
                "}");
    }

    void printInstructions() {
        System.out.println("**");
        System.out.println("usage:");
        System.out.println("java -jar <options>");
        System.out.println("Options:");
        System.out.println("-h    : Print out the help instructions for how to use this application.");
        System.out.println("-p <path>    : The absolute path to the config.json file to be supplied.");
        System.out.println("-example    : View a valid config.json file.");
        System.out.println("**");
    }
}
