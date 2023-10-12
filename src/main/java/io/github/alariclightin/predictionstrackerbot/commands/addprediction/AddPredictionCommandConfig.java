package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlerBuilder;

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
    
}
