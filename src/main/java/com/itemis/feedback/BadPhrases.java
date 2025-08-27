package com.itemis.feedback;

public enum BadPhrases {
    SCHLECHT("schlecht"),
    DEFEKT("defekt"),
    KAPUTT("kaputt"),
    NICHT_OK("nicht ok"),
    MUELL("müll");

    private String badPhrase;

    BadPhrases(String badPhrase) {
        this.badPhrase = badPhrase;
    }

    public String getBadPhrase() {
        return badPhrase;
    }
}
