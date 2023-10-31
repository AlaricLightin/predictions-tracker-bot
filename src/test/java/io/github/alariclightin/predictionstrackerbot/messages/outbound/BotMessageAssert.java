package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.InstanceOfAssertFactories;

public class BotMessageAssert {
    
    public static void assertIsTextBotMessageWithId(BotMessage message, String messageId) {
        assertThat(message)
            .asInstanceOf(InstanceOfAssertFactories.type(BotTextMessage.class))
            .extracting(BotTextMessage::textDataList)
            .asList()
            .hasSize(1)
            .element(0)
            .extracting("messageId")
            .isEqualTo(messageId);
    }
}
