package com.itemis.feedback;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;

public class SendEMailTasklet implements Tasklet {

    private File badFeedbackFile;
    private String eMailAddress;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        System.out.println(" ==> Sending eMail to " + eMailAddress + "with content from file " + badFeedbackFile.getPath());
        return null;
    }

    public void setBadFeedbackFile(File badFeedbackFile) {
        this.badFeedbackFile = badFeedbackFile;
    }

    public void setEMailAddress(String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }
}
