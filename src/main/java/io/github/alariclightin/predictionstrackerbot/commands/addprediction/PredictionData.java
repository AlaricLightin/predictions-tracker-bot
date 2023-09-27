package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.LocalDate;

class PredictionData {
    private String text;
    private LocalDate date;
    private int probability;
    
    String getText() {
        return text;
    }
    LocalDate getDate() {
        return date;
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

    PredictionData addProbability(int probability) {
        this.probability = probability;
        return this;
    }
}
