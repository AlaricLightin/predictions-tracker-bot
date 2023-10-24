package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

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
        UserTextMessage incomingMessage = TestUtils.createTextMessage("test");
        BotMessage botMessage = mock(BotMessage.class);
        when(messageHandlingService.handleTextMessage(incomingMessage)).thenReturn(botMessage);
        SendMessage resultMessage = mock(SendMessage.class);
        when(sendMessageService.create(TestUtils.CHAT_ID, TestUtils.LANGUAGE_CODE, botMessage))
            .thenReturn(resultMessage);

        updateHandlerService.handleTextMessage(incomingMessage);
        verify(outcomingMessageGateway).sendMessage(resultMessage);
    }

}
