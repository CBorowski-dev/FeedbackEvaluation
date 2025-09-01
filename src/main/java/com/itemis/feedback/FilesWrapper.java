package com.itemis.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class FilesWrapper {

    Logger logger = LoggerFactory.getLogger(FilesWrapper.class);

    public void move(java.nio.file.Path source, java.nio.file.Path target) throws IOException {
        logger.info(" ==> Move feedback.csv file to processed directory");
        Files.move(source, target);
    }

}
