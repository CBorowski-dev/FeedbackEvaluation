package com.itemis.feedback;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import java.io.File;

@Configuration
public class FeedbackItemWriter implements ItemWriter<Feedback> {

    private FlatFileItemWriter<Feedback> itemWriter;

    public FeedbackItemWriter() {
        super();

        // LineAggregator, der die Felder der Bean zu einer Zeile zusammenfasst
        DelimitedLineAggregator<Feedback> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(";");

        // FieldExtractor, der die Werte aus der Bean extrahiert
        BeanWrapperFieldExtractor<Feedback> fieldExtractor = new BeanWrapperFieldExtractor<>();
        // Namen der Felder in der Bean, die extrahiert werden sollen
        fieldExtractor.setNames(new String[]{"id", "productId", "date", "stars", "feedbackText"});

        lineAggregator.setFieldExtractor(fieldExtractor);

        // Dateipfad zum Schreiben der Ausgabe festlegen
        // Verwende ein dynamisches File-Objekt, um den Pfad einfacher zu verwalten
        File outputFile = new File("resources/output_feedback.csv");

        itemWriter =  new FlatFileItemWriterBuilder<Feedback>()
                .name("feedbackItemWriter")
                .resource(new FileSystemResource(outputFile))
                .lineAggregator(lineAggregator)
                // Überschreibt die Datei, falls sie bereits existiert
                .append(false)
                .headerCallback(writer -> writer.write("ID;Product_ID;Date;Stars;Feedback"))
                .build();
        itemWriter.open(new ExecutionContext());
    }

    @Override
    public void write(Chunk<? extends Feedback> chunk) throws Exception {
        itemWriter.write(chunk);
    }
}
