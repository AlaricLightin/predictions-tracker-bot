package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class ProbabilityPhase implements MessageHandler {
    private final PredictionDbService predictionDbService;

    ProbabilityPhase(PredictionDbService predictionDbService) {
        this.predictionDbService = predictionDbService;
    }

    @Override
    public MessageHandlingResult handle(Message message, WaitedResponseState state) {
        if (! (state.data() instanceof PredictionData data))
            throw new IllegalStateException("Wrong data type for user " + message.getFrom().getId());

        long userId = message.getFrom().getId();
        data.addProbability(Integer.parseInt(message.getText()));
        Question question = new Question(
                data.getText(),
                // TODO: make timezone configurable
                // TODO add time
                data.getDate().atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC),
                userId);
                
        Prediction prediction = new Prediction(question, userId,
                Instant.ofEpochSecond(message.getDate()));
        predictionDbService.addPrediction(question, prediction);
        return new MessageHandlingResult(
            new BotTextMessage(
                "bot.responses.prediction-added",
                data.getText(), data.getDate(), data.getProbability()),
            null);
    }

    @Override
    public String getCommandName() {
        return AddPredictionConsts.COMMAND_NAME;
    }

    @Override
    public String getPhaseName() {
        return AddPredictionConsts.PROBABILITY_PHASE;
    }
    
}
