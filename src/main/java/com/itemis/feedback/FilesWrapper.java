package com.itemis.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FilesWrapper {

    Logger logger = LoggerFactory.getLogger(FilesWrapper.class);

    public void move(String source, String target) throws IOException {
        logger.info(" ==> Move feedback.csv file to processed directory");
        Files.move(Paths.get(source), Paths.get(target));
    }

}
