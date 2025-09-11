package com.itemis.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackItemProcessor implements ItemProcessor<Feedback, Feedback> {

    Logger logger = LoggerFactory.getLogger(FeedbackItemProcessor.class);

    /**
     *
     * @param item
     * @return
     */
    @Override
    public Feedback process(Feedback item) {
        logger.info(" ==> Processing one feedback");
        String feedbackText = item.feedbackText().toLowerCase();
        for(BadPhrases bp : BadPhrases.values()) {
            if (feedbackText.contains(bp.getBadPhrase())) return item;
        }
        return null;
    }

}
