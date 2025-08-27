package com.itemis.feedback;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class FeedbackItemReader implements ItemReader<Feedback> {

    private FlatFileItemReader<Feedback> itemReader;

    public FeedbackItemReader() {
        super();

        itemReader = new FlatFileItemReaderBuilder<Feedback>()
                .name("feedbackItemReader")
                // Pfad zur CSV-Datei relativ zum Classpath
                .resource(new FileSystemResource("resources/feedback.csv"))
                // Semikolon als Trennzeichen festlegen
                .delimited()
                .delimiter(";")
                // Die Spaltennamen aus der Header-Zeile der CSV-Datei
                .names(new String[]{"ID", "Product_ID", "Date", "Stars", "Feedback"})
                .linesToSkip(1) // Die Header-Zeile überspringen
                // BeanWrapperFieldSetMapper zum Mapping der Spalten auf die Bean
                .fieldSetMapper(fieldSet -> {
                    return new Feedback(
                            // Manuelles Mapping der Felder, um Flexibilität zu gewährleisten
                            fieldSet.readInt("ID"),
                            fieldSet.readInt("Product_ID"),
                            fieldSet.readString("Date"),
                            fieldSet.readInt("Stars"),
                            fieldSet.readString("Feedback")
                    );
                })
                .build();
        itemReader.open(new ExecutionContext());
    }

    @Override
    public Feedback read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return itemReader.read();
    }
}
