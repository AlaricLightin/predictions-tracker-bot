package io.github.alariclightin.predictionstrackerbot.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.MessageChannels;

@Configuration
class IntegrationConfig {
    
    @Bean
    PublishSubscribeChannel incomingUpdatesChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    PublishSubscribeChannel outcomingMessagesChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

}
