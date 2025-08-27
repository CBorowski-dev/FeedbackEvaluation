package com.itemis.feedback;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackProcessor implements ItemProcessor<Feedback, Feedback> {

    /**
     *
     * @param item
     * @return
     */
    @Override
    public Feedback process(Feedback item) {
        String feedbackText = item.feedbackText().toLowerCase();
        for(BadPhrases bp : BadPhrases.values()) {
            if (feedbackText.contains(bp.getBadPhrase())) return item;
        }
        return null;
    }

}
