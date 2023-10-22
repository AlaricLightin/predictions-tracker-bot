package io.github.alariclightin.predictionstrackerbot.bot;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class BaseCommandsIntegrationTest extends TestWithContainer {
    @MockBean
    private Bot bot;
    
    @Autowired
    private UpdateHandlerService updateHandlerService;

    @Test
    void shouldHandleStartCommand() {
        Update update = BotTestUtils.createTextUpdate("/start");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
        assertThat(response)
            .get()
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("Hello");
    }

    @Test
    void shouldHandleUnpredictedTextMessage() {
        Update update = BotTestUtils.createTextUpdate("test message");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
        assertThat(response)
            .get()
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        Update update = BotTestUtils.createTextUpdate("/invalid");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
        assertThat(response)
            .get()
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("invalid", "is not a valid command");
    }

}
