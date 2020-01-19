## A system for moving files in s3 around.
#### Motivation
This is a tool that can be used to move files around between S3 locations. You can use it to read/write in the commerce test account on an ec2 that uses the price.pipelines.je IAM role or you can supply AWS credentials and read data from a prod account and write it to a test account. If the credentials are not supplied then the program assumes that you will read/write only in the test account. You can specify if you want all files in a particular folder copied, or if you want the files recursively copied. You can also specify a list of specific files that you want read/written. If you don't specify a list of files then the program assumed you want to copy from a specific folder or copy recursively from a specifc folder.

#### Features
- Read in configruation as json file.
- Configure sending file(s) from a folder level in an s3 bucket or from that level recursively or be able to get a specified collection(1..*) of files.
- Be able to get get data from prod and put it in test.
- Configure prefix for new location.
- Log a file that was downloaded, then log when it is written successfully to the target location. Keep this information also in memory.
- Log all files to be transferred before transfer begins.

#### config.json file
config.json
```
{
  "s3OriginBucket":"nike-commerce-test-app-internal",
  "s3DestinationBucket":"nike-commerce-test-app-internal",
  "prefixForNewLocation":"test/prefix/file.txt",
  "copyType":"COPY_FROM_LEVEL",
  "awsCredentials" : {
    "originKey": "asdf",
    "originSecretKey": "asdf",
    "originToken":"asdf",
    "destinationKey":"asdf",
    "destinationSecret":"asdf",
    "destinationToken":"asdf"
  },
  "fileList": ["testFile1.txt", "testFile2.txt"]
}
```
#### config.json file explanation
- s3OriginBucket (String)
  - The s3 origin bucket name.
- s3DestinationBucket (String)
  - The s3 destination bucket name.
- prefixForNewLocation (String)
  - Prefix location for the generated files.
- copyType (Enum -COPY_FROM_LEVEL,COPY_RECURSIVELY,COPY_SPECIFIC_FILES)
  - Describes the type of copy operation to be performed. At a single level, recursively, or specific files.
  - Throws IllegalArgumentException if the value is not COPY_SPECIFIC_FILES and a fileList is also provided.
  - Throws IllegalArgumentException the value is COPY_SPECIFIC_FILES and there is not fileList object
- awsCredentials (Map<String, String>)
  - Must include originKey, originSecretKey, originToken, destinationKey, destinationSecret, destinationToken. These values can be generated with gimme-creds. You can use this if you get to get data from a different account then you are writing to.
- fileList (List<String>)
  - A list of the filenames including prefix of files to copy from.

#### How to get started

1. CD to the root of this project with the terminal application.
2. Ensure you have maven installed by running the command. `mvn -version`
3. If you don't have maven installed then install it. Use this guide to get help with this. [https://github.com/rajivkanaujia/alphaworks/wiki/Installing-Maven]
4. Build the jar with `mvn clean install` 
5. Once you have the jar then create a config.json file like the one above
6. To get help run the command `java -jar <your jar> -h`.
7. To run the application run the command `java -jar <your jar> -p <absolute/path/to/config.json>`