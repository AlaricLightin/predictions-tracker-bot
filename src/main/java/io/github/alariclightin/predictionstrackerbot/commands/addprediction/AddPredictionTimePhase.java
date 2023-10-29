package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class AddPredictionTimePhase implements MessageHandler {
    private final Clock clock;
    private final UserTimezoneService timezoneService;

    AddPredictionTimePhase(
        Clock clock,
        UserTimezoneService timezoneService
    ) {
        this.clock = clock;
        this.timezoneService = timezoneService;
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        if (!(state.data() instanceof PredictionData data)) 
            throw new IllegalStateException("Unexpected state data type: " + state.data().getClass().getName());

        LocalDate date = data.getDate();
        if (date == null)
            throw new IllegalStateException("Date is null");

        String text = message.getText();
        try {
            LocalTime time = LocalTime.parse(text);
            Instant instant = time.atDate(date)
                .atZone(timezoneService.getTimezone(message.getUser().getId()))
                .toInstant();
            if (!Instant.now(clock).isAfter(instant))
                return new ActionResult(
                    new BotTextMessage("bot.responses.ask-probability"), 
                    new WaitedResponseState(
                        AddPredictionConsts.COMMAND_NAME, 
                        AddPredictionConsts.PROBABILITY_PHASE, 
                        data.addInstant(instant)
                    )
                );
            else
                throw new UnexpectedUserMessageException("bot.responses.error.past-date-time");
        } catch (DateTimeParseException e) {
            throw new UnexpectedUserMessageException("bot.responses.error.wrong-time-format");
        }
    }

    @Override
    public String getCommandName() {
        return AddPredictionConsts.COMMAND_NAME;
    }

    @Override
    public String getPhaseName() {
        return AddPredictionConsts.TIME_PHASE;
    }
    
}
