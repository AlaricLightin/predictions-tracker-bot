package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.LocalDate;
import java.time.LocalTime;

class PredictionData {
    private String text;
    private LocalDate date;
    private LocalTime time;
    private int probability;
    
    String getText() {
        return text;
    }

    LocalDate getDate() {
        return date;
    }

    LocalTime getTime() {
        return time;
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

    PredictionData addTime(LocalTime time) {
        this.time = time;
        return this;
    }

    PredictionData addProbability(int probability) {
        this.probability = probability;
        return this;
    }
}
