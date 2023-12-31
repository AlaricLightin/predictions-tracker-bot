package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ResultAction;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;

@Component
class PredictionSaver implements ResultAction<PredictionData> {
    private final PredictionDbService predictionDbService;

    PredictionSaver(PredictionDbService predictionDbService) {

        this.predictionDbService = predictionDbService;
    }

    @Override
    public void apply(UserMessage message, PredictionData data) {
        long userId = message.getUser().getId();
        Question question = new Question(
                data.getText(),
                data.getInstant(),
                userId,
                message.getDateTime());

        Prediction prediction = new Prediction(question, userId,
                message.getDateTime(),
                data.getConfidence());
        predictionDbService.addPrediction(question, prediction);
    }
    
}
