package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;

@Configuration
class MessageCreatingConfig {

    @Bean
    AnswerCallbackQueryCreatorFactory answerCallbackQueryCreatorFactory(
        MessageSource messageSource,
        UserLanguageService userLanguageService
    ) {
        return (callbackQueryId, userId, botCallbackAnswer) ->
            new AnswerCallbackQueryCreator(
                messageSource,
                userLanguageService,
                callbackQueryId,
                userId,
                botCallbackAnswer
            );
    }
    
}
