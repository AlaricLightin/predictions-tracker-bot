package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class DatePhase implements MessageHandler {

    @Override
    public MessageHandlingResult handle(Message message, WaitedResponseState state) {
        if (! (state.data() instanceof PredictionData data))
            throw new IllegalStateException("Wrong data type for user " + message.getFrom().getId());
        
        try {
            LocalDate date = LocalDate.parse(message.getText());
            return new MessageHandlingResult(
                new BotTextMessage("bot.responses.ask-probability"), 
                new WaitedResponseState(AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.PROBABILITY_PHASE, 
                    data.addDate(date)));
        } catch (DateTimeParseException e) {
            // TODO refactor validation
            return new MessageHandlingResult(
                new BotTextMessage("bot.responses.wrong-date-format"),
                state);
        }
    }

    @Override
    public String getCommandName() {
        return AddPredictionConsts.COMMAND_NAME;
    }

    @Override
    public String getPhaseName() {
        return AddPredictionConsts.DATE_PHASE;
    }
    
}
