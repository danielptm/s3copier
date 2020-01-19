package com.danielptm.s3copier.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CopyLogger {
    public static final Logger FILES_STARTED_LOGGER = LogManager.getLogger("FileStarted");
    public static final Logger FILES_COMPLETED_LOGGER = LogManager.getLogger("FilesCompleted");
    public static final Logger PRIMARY_LOGGER = LogManager.getLogger("CopyLogger");
}
