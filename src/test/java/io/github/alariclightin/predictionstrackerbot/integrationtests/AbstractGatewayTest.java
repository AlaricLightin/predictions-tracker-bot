package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

public abstract class AbstractGatewayTest extends TestWithContainer {
    @MockBean
    private TelegramLongPollingBot bot;
    
    @Autowired
    protected IncomingMessageGateway incomingMessageGateway;

    @SpyBean
    protected OutcomingMessageGateway outcomingMessageGateway;

    protected void assertResponse(CharSequence... expectedFragments) {
                ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(outcomingMessageGateway, atLeastOnce()).sendMessage(response.capture());
        assertThat(response.getValue())
            .hasFieldOrPropertyWithValue("chatId", BotTestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains(expectedFragments);
    }

    protected void sendTextUpdate(String text) {
        incomingMessageGateway.handleUpdate(BotTestUtils.createTextUpdate(text));
    }

}
