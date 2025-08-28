package com.itemis.feedback;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class BatchConfiguration {

    private static class FilesWrapper {
        public void move(java.nio.file.Path source, java.nio.file.Path target) throws IOException {
            System.out.println(" ==> Move file");
            Files.move(source, target);
        }
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FeedbackItemReader reader, FeedbackProcessor processor, FeedbackItemWriter writer, BadFeedbackStepExecutionListener listener) {
        return new StepBuilder("step1", jobRepository)
                .<Feedback, Feedback>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter moveFileTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(new FilesWrapper());
        adapter.setTargetMethod("move");
        adapter.setArguments(new Object[]{ Paths.get("resources/feedback.csv"), Paths.get("resources/old/feedback.csv") });

        return adapter;
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(moveFileTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        SendEMailTasklet tasklet = new SendEMailTasklet();
        tasklet.setBadFeedbackFile(new File("resources/old/feedback.csv"));
        tasklet.setEMailAddress("support@itemis.com");
        return new StepBuilder("step3", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        SendEMailTasklet tasklet = new SendEMailTasklet();
        tasklet.setBadFeedbackFile(new File("resources/old/feedback.csv"));
        tasklet.setEMailAddress("product.development@itemis.com");
        return new StepBuilder("step4", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Flow flow1(Step step3) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step3)
                .build();
    }

    @Bean
    public Flow flow2(Step step4) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(step4)
                .build();
    }

    @Bean
    public Job feedbackJob(JobRepository jobRepository, Step step1, Step step2, Flow flow1, Flow flow2, JobCompletionNotificationListener listener) {
        return new JobBuilder("feedbackJob", jobRepository)
                .listener(listener)
                .start(step1)
                .on("COMPLETED_WITH_BAD_FEEDBACK").to(flow1)
                .split(new SimpleAsyncTaskExecutor())
                .add(flow2)
                .next(step2)
                .end()
                .build();
    }

}