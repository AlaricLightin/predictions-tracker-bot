package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.ResultAction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;

@Component
class PredictionSaver implements ResultAction<PredictionData> {
    private final PredictionDbService predictionDbService;

    PredictionSaver(PredictionDbService predictionDbService) {
        this.predictionDbService = predictionDbService;
    }

    @Override
    public void apply(Message message, PredictionData data) {
        long userId = message.getFrom().getId();
        Question question = new Question(
                data.getText(),
                // TODO: make timezone configurable
                data.getDate().atTime(data.getTime()).toInstant(ZoneOffset.UTC),
                userId);

        Prediction prediction = new Prediction(question, userId,
                Instant.ofEpochSecond(message.getDate()),
                data.getProbability());
        predictionDbService.addPrediction(question, prediction);
    }
    
}
