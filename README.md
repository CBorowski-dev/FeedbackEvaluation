# Project FeedbackEvaluation

## Introduction
This project is a demo project for Spring Batch. It uses a simple batch job example to demonstrate how individual Spring Batch 
concepts can be used.

## Spring Batch concepts and where they can be found in the code

### Configuration
The configuration can be done in Java or XML. In the project, the configuration is done in Java in the BatchConfiguration class. 
The same configuration is available in the XML version in the job-config.xml file in the \src\main\resources directory.
To use the XML configuration instead of the Java configuration, the following changes must be made:

1) Comment out the annotation `@ImportResource("classpath:job-config.xml")` in the `FeedbackApplication` class.
2) Remove/uncomment the annotation `@Configuration` in the `BatchConfiguration` class.
3) Remove/uncomment the annotation `@Component` in the `BadFeedbackStepExecutionListener` and `JobCompletionNotificationListener` classes.

### Define a Job
See the `feedbackJob(...)` method in the `BatchConfiguration` class.

### Define a Step
See the `processFeedbackStep(...)` method in the `BatchConfiguration` class.

### ItemReader and FlatFileItemReader
See ​​the `FeedbackItemReader` class.

### ItemProcessor
See ​​the `FeedbackProcessor` class for more information.

### ItemWriter and FlatFileItemWriter
See ​​the `FeedbackItemWriter` class for more information.

### Define TaskletStep
See the `moveFileStep(...)` and `sendEMailToSupportStep(...)` methods in the `BatchConfiguration` class for more information.

### Use TaskletAdapter
See ​​the `moveFileTasklet(...)` method in the `BatchConfiguration` class for more information.

### Define SplitFlow
See the `splitFlow(...)` method in the `BatchConfiguration` class for more information.

## Scheduling
The Spring Boot internal scheduling is enabled using the annotation `@EnableScheduling` in the `FeedbackApplication` class and the 
annotation `@Scheduled(cron = "0 */1 * * * *")` in the `FeedbackJobScheduler` class. Additionally, the entry `spring.batch.job.enabled=false` 
must be set in `application.properties`. This ensures that the contained beans are not initially executed when the Spring Boot 
application is started. It also prevents an unwanted initial job run that was not initiated by the scheduler.

## Running the application
To start the application just type `.\mvnw spring-boot:run` on the command line. The scheduler runs every minute, so you may have to 
wait some time for the first batch job to run. After the first batch job run the file `\resources\feedback.csv` is moved 
to the directory `\resources\processed` and a new file `\resources\bad_feedback.csv` is created.\
If you let the application continue running, the second batch job run will be executed after another minute. However, since this 
run can no longer find the feedback.csv, an exception is thrown. This was accepted during the development of the demo, as the primary 
goal is to demonstrate how a batch job run works. In reality, after such batch job runs, the input and output artifacts are usually removed, 
made available again, or processed in another way. 

## Miscellaneous
> [!WARNING]
> The source code in this project is intentionally kept simple and short. To achieve this, some things, such as paths, are hard-coded.
> Likewise, appropriate exception handling is omitted, and the code is not secured by tests. All in all, **the code is not intended for productive use**.
