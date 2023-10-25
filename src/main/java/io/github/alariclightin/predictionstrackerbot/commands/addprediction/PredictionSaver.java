package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import org.springframework.stereotype.Component;
import io.github.alariclightin.predictionstrackerbot.commands.ResultAction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;

@Component
class PredictionSaver implements ResultAction<PredictionData> {
    private final PredictionDbService predictionDbService;
    private final UserTimezoneService userTimezoneService;

    PredictionSaver(
            PredictionDbService predictionDbService,
            UserTimezoneService userTimezoneService) {

        this.predictionDbService = predictionDbService;
        this.userTimezoneService = userTimezoneService;
    }

    @Override
    public void apply(UserMessage message, PredictionData data) {
        long userId = message.getUser().getId();
        Question question = new Question(
                data.getText(),
                data.getDate()
                    .atTime(data.getTime())
                    .atZone(userTimezoneService.getTimezone(userId))
                    .toInstant(),
                userId);

        Prediction prediction = new Prediction(question, userId,
                message.getDateTime(),
                data.getProbability());
        predictionDbService.addPrediction(question, prediction);
    }
    
}
