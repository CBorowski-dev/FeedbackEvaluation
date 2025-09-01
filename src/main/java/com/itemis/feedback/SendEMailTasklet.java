package com.itemis.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;

public class SendEMailTasklet implements Tasklet {

    private File badFeedbackFile;
    private String eMailAddress;

    Logger logger = LoggerFactory.getLogger(SendEMailTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        logger.info(" ==> Sending eMail to " + eMailAddress + "with content from file " + badFeedbackFile.getPath());
        return RepeatStatus.FINISHED;
    }

    public void setBadFeedbackFile(File badFeedbackFile) {
        this.badFeedbackFile = badFeedbackFile;
    }

    public void setEMailAddress(String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }
}
