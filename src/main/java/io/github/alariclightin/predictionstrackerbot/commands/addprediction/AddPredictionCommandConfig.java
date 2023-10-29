package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;

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
    MessageHandler addPredictionTextPhase(DeadlinePromptService deadlinePromptService) {
        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.TEXT_PHASE)
            .setResponseMessageFunc((message, data) -> 
                    deadlinePromptService.getDeadlinePromptMessage())
            .setNextPhase(AddPredictionConsts.DATE_PHASE)
            .setStateUpdater((text, data) -> data.addText(text))
            .build();
    }

    private static InlineButton createButton(
        String messageId,
        DateTimeAction action) {

        return new InlineButton(messageId, 
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.DATE_PHASE, action.toString());
    }

    private static final BotKeyboard DATE_TIME_KEYBOARD = new BotKeyboard(
        List.of(
            List.of(
                // TODO show first button only for debug
                createButton("bot.buttons.one-minute", DateTimeAction.ONE_MINUTE),
                createButton("bot.buttons.one-hour", DateTimeAction.ONE_HOUR)
            ),
            List.of(
                createButton("bot.buttons.today", DateTimeAction.TODAY),
                createButton("bot.buttons.tomorrow", DateTimeAction.TOMORROW)
            ),
            List.of(
                createButton("bot.buttons.next-month", DateTimeAction.NEXT_MONTH),
                createButton("bot.buttons.next-year", DateTimeAction.NEXT_YEAR)
            )
        )
    );

    @Bean
    DeadlinePromptService deadlinePromptService() {
        return () -> new BotMessageList(
            new BotTextMessage("bot.responses.ask-deadline"),
            DATE_TIME_KEYBOARD
        );
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
                        throw new UnexpectedUserMessageException("bot.responses.error.probability-out-of-range");
                    return data.addProbability(probability);
                }
                catch (NumberFormatException e) {
                    throw new UnexpectedUserMessageException("bot.responses.error.propability-not-a-number");
                }
            })
            
            .setResponseMessageFunc((message, data) -> {
                return new BotTextMessage(
                    "bot.responses.prediction-added",
                    data.getText(), data.getInstant(), data.getProbability());
            })
            
            .setResultAction(predictionSaver)
            .build();
    }
    
}
