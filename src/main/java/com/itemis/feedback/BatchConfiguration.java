package com.itemis.feedback;

import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class BatchConfiguration {

    private static class FilesWrapper {
        public void move(java.nio.file.Path source, java.nio.file.Path target) throws IOException {
            Files.move(source, target);
        }
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FeedbackItemReader reader, FeedbackProcessor processor, FeedbackItemWriter writer) {
        return new StepBuilder("step1", jobRepository)
                .<Feedback, Feedback>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
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
    public Job feedbackJob(JobRepository jobRepository, Step step1, Step step2, JobCompletionNotificationListener listener) {
        return new JobBuilder("feedbackJob", jobRepository)
                .listener(listener)
                .start(step1)
                .next(step2)
                .build();
    }

}