package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class AddPredictionDatePhase implements MessageHandler {

    private final UserTimezoneService timezoneService;
    private final Clock clock;

    AddPredictionDatePhase(
        Clock clock,
        UserTimezoneService timezoneService
    ) {

        this.timezoneService = timezoneService;
        this.clock = clock;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        if (!(state.data() instanceof PredictionData data)) 
            throw new IllegalStateException("Unexpected state data type: " + state.data().getClass().getName());

        String text = message.getText();
        ZoneId zoneId = timezoneService.getTimezone(message.getUser().getId());

        try {
            LocalDateTime dateTime = LocalDateTime.parse(text, DATE_TIME_FORMATTER);
            Instant instant = dateTime.atZone(zoneId).toInstant();
            if (!Instant.now(clock).isAfter(instant))
                return createResultForDateTime(data.addInstant(instant));
            else
                throw new UnexpectedUserMessageException("bot.responses.error.past-date-time");
        }
        catch (DateTimeParseException e) {
            //
        }

        try {
            LocalDate date = LocalDate.parse(text, DATE_FORMATTER);
            if (!currentLocalDate(zoneId).isAfter(date))
                return createResultForDate(data.addDate(date));
            else
                throw new UnexpectedUserMessageException("bot.responses.error.past-date-time");
        }
        catch (DateTimeParseException e) {
            //
        }
        
        DateTimeAction action;
        try {
            action = DateTimeAction.valueOf(text.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new UnexpectedUserMessageException("bot.responses.error.wrong-date-time-format");
        }

        switch (action) {
            case ONE_MINUTE:
                return createResultForDateTime(data.addInstant(
                    currentLocalDateTime(zoneId)
                        .plusMinutes(1)
                        .atZone(zoneId)
                        .toInstant()
                ));
            
            case ONE_HOUR:
                return createResultForDateTime(data.addInstant(
                    currentLocalDateTime(zoneId)
                        .plusHours(1)
                        .atZone(zoneId)
                        .toInstant()
                ));
                
            case TODAY:
                return createResultForDate(data.addDate(currentLocalDate(zoneId)));

            case TOMORROW:
                return createResultForDate(data.addDate(currentLocalDate(zoneId).plusDays(1)));

            case NEXT_MONTH:
                return createResultForDate(data.addDate(
                    currentLocalDate(zoneId)
                        .with(TemporalAdjusters.firstDayOfNextMonth())
                ));

            case NEXT_YEAR:
                return createResultForDate(data.addDate(
                    currentLocalDate(zoneId)
                        .with(TemporalAdjusters.firstDayOfNextYear())
                ));

            default:
                throw new UnexpectedUserMessageException("bot.responses.error.wrong-date-time-format");
        }    
    }

    private LocalDate currentLocalDate(ZoneId zoneId) {
        return LocalDate.ofInstant(clock.instant(), zoneId);
    }

    private LocalDateTime currentLocalDateTime(ZoneId zoneId) {
        return LocalDateTime.ofInstant(clock.instant(), zoneId);
    }

    private ActionResult createResultForDateTime(PredictionData data) {
        return new ActionResult(
            new BotTextMessage("bot.responses.ask-confidence"),
            new WaitedResponseState(
                AddPredictionConsts.COMMAND_NAME, 
                AddPredictionConsts.CONFIDENCE_PHASE, 
                data
            )
        );
    }

    private ActionResult createResultForDate(PredictionData data) {
        return new ActionResult(
            new BotTextMessage("bot.responses.ask-deadline-time"),
            new WaitedResponseState(
                AddPredictionConsts.COMMAND_NAME, 
                AddPredictionConsts.TIME_PHASE, 
                data
            )
        );
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
