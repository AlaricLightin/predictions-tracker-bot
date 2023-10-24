package io.github.alariclightin.predictionstrackerbot.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;

@Configuration
class IntegrationConfig {
    
    @Bean
    PublishSubscribeChannel outcomingMessagesChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    PublishSubscribeChannel incomingUserMessageChannel() {
        PublishSubscribeChannel result = MessageChannels.publishSubscribe().getObject();
        result.setDatatypes(UserTextMessage.class);
        return result;
    }

    @Bean
    PublishSubscribeChannel incomingCallbackQueryChannel() {
        PublishSubscribeChannel result = MessageChannels.publishSubscribe().getObject();
        result.setDatatypes(ButtonCallbackQuery.class);
        return result;
    }

    @Bean
    IntegrationFlow telegramUpdateRouterFlow() {
        return IntegrationFlow.from("incomingUpdatesChannel")
            .route(Update.class, update -> {
                if (update.getMessage() != null) {
                    return "incomingUserMessageChannel";
                }
                // TODO add additional checks?
                else if (update.getCallbackQuery() != null) {
                    return "incomingCallbackQueryChannel";
                }
                else return "nullChannel";
            })
            .get();
    }

    @Bean
    @IntegrationConverter
    IncomingMessageConverter incomingMessageConverter() {
        return new IncomingMessageConverter();
    }

    private static class IncomingMessageConverter implements Converter<Update, UserTextMessage> {
        @Override
        public UserTextMessage convert(Update update) {
            return new UserTextMessage(update.getMessage());
        }
    }

    @Bean
    @IntegrationConverter
    IncomingButtonCallbackConverter incomingButtonCallbackConverter() {
        return new IncomingButtonCallbackConverter();
    }

    private static class IncomingButtonCallbackConverter implements Converter<Update, ButtonCallbackQuery> {
        @Override
        public ButtonCallbackQuery convert(Update update) {
            return new ButtonCallbackQuery(update.getCallbackQuery());
        }
    }
}
