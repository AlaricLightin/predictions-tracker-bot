package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

@Configuration
class AddPredictionCommandConfig {

    @Bean
    MessageHandler addPredictionCommand() {
        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setNextPhasePromptMessageId("bot.responses.ask-prediction-text")
            .setNextPhase(AddPredictionConsts.TEXT_PHASE)
            .setStateUpdater((text, data) -> new PredictionData())
            .build();
    }

    @Bean
    MessageHandler addPredictionTextPhase() {
        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.TEXT_PHASE)
            .setNextPhasePromptMessageId("bot.responses.ask-deadline")
            .setNextPhase(AddPredictionConsts.DATE_PHASE)
            .setStateUpdater((text, data) -> data.addText(text))
            .build();
    }

    @Bean
    MessageHandler addPredictionDatePhase() {
        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.DATE_PHASE)
            .setNextPhasePromptMessageId("bot.responses.ask-deadline-time")
            .setNextPhase(AddPredictionConsts.TIME_PHASE)
            .setStateUpdater((text, data) -> {
                try {
                    LocalDate date = LocalDate.parse(text);
                    return data.addDate(date);
                }
                catch (DateTimeParseException e) {
                    throw new UnexpectedMessageException("bot.responses.error.wrong-date-format");
                }
            })
            .build();
    }

    @Bean
    MessageHandler addPredictionTimePhase() {
        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.TIME_PHASE)
            .setNextPhasePromptMessageId("bot.responses.ask-probability")
            .setNextPhase(AddPredictionConsts.PROBABILITY_PHASE)
            .setStateUpdater((text, data) -> {
                try {
                    LocalTime time = LocalTime.parse(text);
                    return data.addTime(time);
                }
                catch (DateTimeParseException e) {
                    throw new UnexpectedMessageException("bot.responses.error.wrong-time-format");
                }
            })
            .build();
    }

    @Bean
    MessageHandler addPredictionProbabilityPhase(
        PredictionSaver predictionSaver
    ) {

        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.PROBABILITY_PHASE)
            
            .setStateUpdater((text, data) -> {
                try {
                    int probability = Integer.parseInt(text);
                    if (probability <= 0 || probability >= 100)
                        throw new UnexpectedMessageException("bot.responses.error.probability-out-of-range");
                    return data.addProbability(probability);
                }
                catch (NumberFormatException e) {
                    throw new UnexpectedMessageException("bot.responses.error.propability-not-a-number");
                }
            })
            
            .setResponseMessageFunc((message, data) -> {
                LocalDateTime datetime = LocalDateTime.of(data.getDate(), data.getTime());
                return new BotTextMessage(
                    "bot.responses.prediction-added",
                    data.getText(), datetime, data.getProbability());
            })
            
            .setResultAction(predictionSaver)
            .build();
    }
    
}
