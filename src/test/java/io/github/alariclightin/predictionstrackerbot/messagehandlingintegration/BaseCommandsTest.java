package io.github.alariclightin.predictionstrackerbot.messagehandlingintegration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class BaseCommandsTest extends TestWithContainer {
    @MockBean
    private Bot bot;
    
    @Autowired
    private MessageHandlingService messageHandlingService;

    @Test
    void shouldHandleStartCommand() {
        Message message = TestUtils.createTestMessage("/start");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("Hello");
    }

    @Test
    void shouldHandleUnpredictedTextMessage() {
        Message message = TestUtils.createTestMessage("test message");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        Message message = TestUtils.createTestMessage("/invalid");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("invalid", "is not a valid command");
    }

}
