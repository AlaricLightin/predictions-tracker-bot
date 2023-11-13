package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Configuration
class SetLanguageCommandConfig {
    
    @Bean
    MessageHandler setLanguageCommand() {
        return new MessageHandlerBuilder<String>()
            .setCommandName(SetLanguageConsts.COMMAND)
            .setNextPhasePromptMessage(
                new BotTextMessage(
                    BotKeyboard.createOneRowKeyboard(LanguageAction.values()),
                    "bot.responses.setlanguage.ask-language"
                )
            )
            .setNextPhase(SetLanguageConsts.DATA_INPUT_PHASE)
            .setStateUpdater((text, data) -> "")
            .build();
    }
}
