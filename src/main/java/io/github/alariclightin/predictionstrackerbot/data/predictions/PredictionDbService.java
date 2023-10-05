package io.github.alariclightin.predictionstrackerbot.data.predictions;

public interface PredictionDbService {

    void addPrediction(Question question, Prediction prediction);

}
