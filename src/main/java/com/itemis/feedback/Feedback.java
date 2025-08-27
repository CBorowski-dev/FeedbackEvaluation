package com.itemis.feedback;

public record Feedback(int id, int productId, String date, int stars, String feedbackText) {

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", productId=" + productId +
                ", date='" + date + '\'' +
                ", stars=" + stars +
                ", feedbackText='" + feedbackText + '\'' +
                '}';
    }

}
