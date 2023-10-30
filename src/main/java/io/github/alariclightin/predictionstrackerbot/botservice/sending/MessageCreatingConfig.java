package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MessageCreatingConfig {

    @Bean
    AnswerCallbackQueryCreatorFactory answerCallbackQueryCreatorFactory(
        MessageSource messageSource
    ) {
        return (callbackQueryId, languageCode, botCallbackAnswer) ->
            new AnswerCallbackQueryCreator(
                messageSource,
                callbackQueryId,
                languageCode,
                botCallbackAnswer
            );
    }
    
}
