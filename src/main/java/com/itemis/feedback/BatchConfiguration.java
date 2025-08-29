package com.itemis.feedback;

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
import java.nio.file.Paths;

@Configuration
public class BatchConfiguration {

    @Bean
    public Step processFeedbackStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                                    FeedbackItemReader reader, FeedbackProcessor processor, FeedbackItemWriter writer, BadFeedbackStepExecutionListener listener) {
        return new StepBuilder("processFeedbackStep", jobRepository)
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
        adapter.setArguments(new Object[]{Paths.get("resources/feedback.csv"), Paths.get("resources/old/feedback.csv")});

        return adapter;
    }

    @Bean
    public Step moveFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("moveFileStep", jobRepository)
                .tasklet(moveFileTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step sendEMailToSupportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        SendEMailTasklet tasklet = new SendEMailTasklet();
        tasklet.setBadFeedbackFile(new File("resources/old/feedback.csv"));
        tasklet.setEMailAddress("support@itemis.com");
        return new StepBuilder("sendEMailToSupportStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step sendEMailToDevelopmentStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        SendEMailTasklet tasklet = new SendEMailTasklet();
        tasklet.setBadFeedbackFile(new File("resources/old/feedback.csv"));
        tasklet.setEMailAddress("product.development@itemis.com");
        return new StepBuilder("sendEMailToDevelopmentStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Flow flow1(Step sendEMailToSupportStep) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(sendEMailToSupportStep)
                .build();
    }

    @Bean
    public Flow flow2(Step sendEMailToDevelopmentStep) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(sendEMailToDevelopmentStep)
                .build();
    }

    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2, Step moveFileStep) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2)
                .next(moveFileStep)
                .build();
    }

    @Bean
    public Job feedbackJob(JobRepository jobRepository, Step processFeedbackStep, Step moveFileStep, Flow splitFlow, JobCompletionNotificationListener listener) {
        return new JobBuilder("feedbackJob", jobRepository)
                .listener(listener)
                .start(processFeedbackStep)
                .on("COMPLETED_WITH_BAD_FEEDBACK").to(splitFlow)
                .from(processFeedbackStep).on("COMPLETED").to(moveFileStep)
                .end()
                .build();
    }

}