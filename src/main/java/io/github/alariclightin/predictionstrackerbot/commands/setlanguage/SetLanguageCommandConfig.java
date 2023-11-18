package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.HandlersSequence;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.SimpleInputCommandBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.MessageSourceArgument;

@Configuration
class SetLanguageCommandConfig {
    
    @Bean
    HandlersSequence setLanguageSequence(UserLanguageService userLanguageService) {

        return new SimpleInputCommandBuilder<LanguageAction>(LanguageAction.COMMAND_NAME)
            .setPromptMessage(
                new BotTextMessage(
                    BotKeyboard.createOneRowKeyboard(LanguageAction.values()),
                    "bot.responses.setlanguage.ask-language"
                )
            )
            
            .setResponseMessageFunc((message, action) -> 
                new BotTextMessage("bot.responses.setlanguage.language-is-set", 
                    new MessageSourceArgument(action.messageId())
                )
            )

            .setResultFunction(text -> {
                try {
                    return LanguageAction.valueOf(text.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new UnexpectedUserMessageException("bot.responses.error.wrong-language");
                }
            })

            .setResultAction((message, action) -> 
                userLanguageService.setLanguageCode(message.getUser().getId(), action.languageCode())
            )

            .build();
            
    }

}
