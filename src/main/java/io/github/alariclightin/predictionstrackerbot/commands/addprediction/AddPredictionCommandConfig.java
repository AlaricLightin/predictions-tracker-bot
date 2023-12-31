package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

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

    private static final BotKeyboard DATE_TIME_KEYBOARD = new BotKeyboard(
        List.of(
            List.of(
                // TODO show first button only for debug
                DateTimeAction.ONE_MINUTE,
                DateTimeAction.ONE_HOUR
            ),
            List.of(
                DateTimeAction.TODAY, DateTimeAction.TOMORROW
            ),
            List.of(
                DateTimeAction.NEXT_MONTH, DateTimeAction.NEXT_YEAR
            )
        )
    );

    @Bean
    DeadlinePromptService deadlinePromptService() {
        return () -> new BotTextMessage(
            DATE_TIME_KEYBOARD,     
            "bot.responses.ask-deadline"
        );
    }

    @Bean
    MessageHandler addPredictionConfidencePhase(
        PredictionSaver predictionSaver
    ) {

        return new MessageHandlerBuilder<PredictionData>()
            .setCommandName(AddPredictionConsts.COMMAND_NAME)
            .setPhaseName(AddPredictionConsts.CONFIDENCE_PHASE)
            
            .setStateUpdater((text, data) -> {
                try {
                    int confidence = Integer.parseInt(text);
                    if (confidence <= 0 || confidence >= 100)
                        throw new UnexpectedUserMessageException("bot.responses.error.confidence-out-of-range");
                    return data.addConfidence(confidence);
                }
                catch (NumberFormatException e) {
                    throw new UnexpectedUserMessageException("bot.responses.error.confidence-not-a-number");
                }
            })
            
            .setResponseMessageFunc((message, data) -> {
                return new BotTextMessage(
                    "bot.responses.prediction-added",
                    data.getText(), data.getInstant(), data.getConfidence());
            })
            
            .setResultAction(predictionSaver)
            .build();
    }
    
}
