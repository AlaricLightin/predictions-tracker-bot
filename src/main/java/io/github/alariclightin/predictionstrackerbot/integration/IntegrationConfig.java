package io.github.alariclightin.predictionstrackerbot.integration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.router.PayloadTypeRouter;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.ExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotFile;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Configuration
class IntegrationConfig {
    
    @Bean
    PublishSubscribeChannel outcomingMessagesChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }


    @Bean
    PublishSubscribeChannel outcomingFileMessagesChannel() {
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
                    return "incomingUpdateWithMessagesChannel";
                }
                else if (update.getCallbackQuery() != null 
                    && ButtonCallbackQuery.isButtonCallbackQuery(update.getCallbackQuery())) {
                    
                    return "incomingUpdateWithCallbackQueryChannel";
                }
                else return "nullChannel";
            })
            .get();
    }

    @Bean
    PublishSubscribeChannel afterHandlingChannel() {
        PublishSubscribeChannel result = MessageChannels.publishSubscribe().getObject();
        result.setDatatypes(BotMessage.class, BotCallbackAnswer.class);
        return result;
    }

    @ServiceActivator(inputChannel = "afterHandlingChannel")
    @Bean
    PayloadTypeRouter afterHandlingRouter() {
        var result = new PayloadTypeRouter();
        result.setChannelMapping(BotTextMessage.class.getName(), "botMessageChannel");
        result.setChannelMapping(BotFile.class.getName(), "botFileChannel");
        result.setChannelMapping(BotCallbackAnswer.class.getName(), "botCallbackAnswerChannel");
        return result;
    }

    @Bean
    @Transformer(inputChannel = "incomingUpdateWithMessagesChannel", outputChannel = "incomingUserMessageChannel")
    HeaderEnricher updateWithMessagHeaderEnricher() {
        Map<String, HeaderValueMessageProcessor<?>> headersToAdd = new HashMap<>();

        Expression chatIdExpression = new SpelExpressionParser().parseExpression(
            "payload.getMessage().getChatId()");
        headersToAdd.put("chatId", 
            new ExpressionEvaluatingHeaderValueMessageProcessor<>(chatIdExpression, String.class));

        return new HeaderEnricher(headersToAdd);
    }

    @Bean
    @Transformer(
        inputChannel = "incomingUpdateWithCallbackQueryChannel", outputChannel = "incomingCallbackQueryChannel")
    HeaderEnricher updateWithCallbackQueryHeaderEnricher() {
        Map<String, HeaderValueMessageProcessor<?>> headersToAdd = new HashMap<>();

        Expression callbackIdExpression = new SpelExpressionParser().parseExpression(
            "payload.getCallbackQuery().getId()");
        headersToAdd.put("callbackId", 
            new ExpressionEvaluatingHeaderValueMessageProcessor<>(callbackIdExpression, String.class));

        Expression chatIdExpression = new SpelExpressionParser().parseExpression(
            "payload.getCallbackQuery().getFrom().getId()");
        headersToAdd.put("chatId", 
            new ExpressionEvaluatingHeaderValueMessageProcessor<>(chatIdExpression, String.class));

        return new HeaderEnricher(headersToAdd);
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
