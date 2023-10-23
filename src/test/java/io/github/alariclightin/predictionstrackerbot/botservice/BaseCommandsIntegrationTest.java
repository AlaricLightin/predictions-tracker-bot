package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class BaseCommandsIntegrationTest extends TestWithContainer {
    @MockBean
    private TelegramLongPollingBot bot;
    
    @Autowired
    private IncomingMessageGateway incomingMessageGateway;

    @SpyBean
    private OutcomingMessageGateway outcomingMessageGateway;

    @Test
    void shouldHandleStartCommand() {
        Update update = BotTestUtils.createTextUpdate("/start");
        incomingMessageGateway.handleUpdate(update);
        
        assertResponse("Hello");
    }

    @Test
    void shouldHandleUnpredictedTextMessage() {
        Update update = BotTestUtils.createTextUpdate("test message");
        incomingMessageGateway.handleUpdate(update);

        assertResponse("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        Update update = BotTestUtils.createTextUpdate("/invalid");
        incomingMessageGateway.handleUpdate(update);
        
        assertResponse("invalid", "is not a valid command");
    }

    private void assertResponse(CharSequence... expectedFragments) {
                ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(outcomingMessageGateway).sendMessage(response.capture());
        assertThat(response.getValue())
            .hasFieldOrPropertyWithValue("chatId", BotTestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains(expectedFragments);
    }

}
