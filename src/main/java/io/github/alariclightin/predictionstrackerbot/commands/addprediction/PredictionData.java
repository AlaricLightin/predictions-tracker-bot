package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.Instant;
import java.time.LocalDate;

class PredictionData {
    private String text;
    private LocalDate date;
    private Instant instant;
    private int probability;
    
    String getText() {
        return text;
    }

    LocalDate getDate() {
        return date;
    }

    Instant getInstant() {
        return instant;
    }
    
    int getProbability() {
        return probability;
    }

    PredictionData addText(String text) {
        this.text = text;
        return this;
    }

    PredictionData addDate(LocalDate date) {
        this.date = date;
        return this;
    }

    PredictionData addInstant(Instant instant) {
        this.instant = instant;
        return this;
    }

    PredictionData addProbability(int probability) {
        this.probability = probability;
        return this;
    }
}
