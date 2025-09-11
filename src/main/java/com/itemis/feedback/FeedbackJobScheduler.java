package com.itemis.feedback;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeedbackJobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job feedbackJob;

    @Scheduled(cron = "0 */1 * * * *") // Runs the job every minute
    public void scheduleMyBatchJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        System.out.println(" ============ start ===========");
        jobLauncher.run(feedbackJob, new JobParametersBuilder().addDate("timestamp", new Date()).toJobParameters());
    }
}
