package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integrationtests.BotTestUtils;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

public class UpdateHandlerServiceImplTest {
    private UpdateHandlerService updateHandlerService;
    private MessageHandlingService messageHandlingService;
    private SendMessageService sendMessageService;
    private OutcomingMessageGateway outcomingMessageGateway;

    @BeforeEach
    void setUp() {
        messageHandlingService = mock(MessageHandlingService.class);
        sendMessageService = mock(SendMessageService.class);
        outcomingMessageGateway = mock(OutcomingMessageGateway.class);
        updateHandlerService = new UpdateHandlerService(
            messageHandlingService, sendMessageService, outcomingMessageGateway);
    }

    @Test
    void shouldHandleTextUpdate() {
        Update update = BotTestUtils.createTextUpdate("test");
        BotMessage botMessage = mock(BotMessage.class);
        when(messageHandlingService.handleTextMessage(argThat(message ->
            message.getText().equals("test") && message.getUser().getId() == BotTestUtils.CHAT_ID
        ))).thenReturn(botMessage);
        SendMessage resultMessage = mock(SendMessage.class);
        when(sendMessageService.create(BotTestUtils.CHAT_ID, BotTestUtils.LANGUAGE_CODE, botMessage))
            .thenReturn(resultMessage);

        updateHandlerService.handleUpdate(update);
        verify(outcomingMessageGateway).sendMessage(resultMessage);
    }

    @Test
    void shouldNotHandleOtherUpdate() {
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(null);

        updateHandlerService.handleUpdate(update);
        verify(outcomingMessageGateway, never()).sendMessage(any());
    }
}
