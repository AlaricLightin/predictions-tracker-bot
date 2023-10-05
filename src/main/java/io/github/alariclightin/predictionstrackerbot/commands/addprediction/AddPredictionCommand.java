package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.AbstractCommand;
import io.github.alariclightin.predictionstrackerbot.commands.WaitedResponseHandler;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class AddPredictionCommand extends AbstractCommand implements WaitedResponseHandler {
    private final StateHolderService stateHolderService;
    private final PredictionDbService predictionDbService;

    AddPredictionCommand(StateHolderService stateHolderService,
            PredictionDbService predictionDbService) {
        this.stateHolderService = stateHolderService;
        this.predictionDbService = predictionDbService;
    }

    @Override
    public BotMessage handleCommand(Message message) {
        var userId = getUserId(message);
        WaitedResponseState state = createWaitedResponseState(AddPredictionPhase.TEXT, new PredictionData());
        stateHolderService.saveState(userId, state);
        return new BotTextMessage("bot.responses.ask-prediction-text");
    }

    @Override
    public String getCommandName() {
        return "add";
    }

    @Override
    // TODO make more generic and add validations
    public BotMessage handleWaitedResponse(Message message) {
        long userId = getUserId(message);
        WaitedResponseState state = stateHolderService.getState(userId);
        if (state == null)
            throw new IllegalStateException("No state for user " + userId);
        
        if (!(state.data() instanceof PredictionData data))
            throw new IllegalStateException("Wrong data type for user " + userId);

        AddPredictionPhase phase = AddPredictionPhase.valueOf(state.phase());
        switch (phase) {
            case TEXT:
                stateHolderService.saveState(userId, 
                    createWaitedResponseState(AddPredictionPhase.DATE, data.addText(message.getText())));
                return new BotTextMessage("bot.responses.ask-deadline");

            case DATE:
                try {
                    LocalDate date = LocalDate.parse(message.getText());
                    stateHolderService.saveState(userId, 
                        createWaitedResponseState(AddPredictionPhase.PROBABILITY, data.addDate(date)));
                    return new BotTextMessage("bot.responses.ask-probability");

                }
                catch (DateTimeParseException e) {
                    return new BotTextMessage("bot.responses.wrong-date-format");
                }
                
            case PROBABILITY:
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
                stateHolderService.deleteState(userId);
                return new BotTextMessage(
                        "bot.responses.prediction-added",
                        data.getText(), data.getDate(), data.getProbability()
                    );

            default: throw new IllegalStateException("Unknown phase when adding prediction: " + phase);
        }
    }
    
    private WaitedResponseState createWaitedResponseState(
        AddPredictionPhase phase, PredictionData data) {
        return new WaitedResponseState(getCommandName(), phase.toString(), data);
    }
}
