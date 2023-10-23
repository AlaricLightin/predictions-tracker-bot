package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

public class UpdateHandlerServiceImplTest {
    private UpdateHandlerServiceImpl updateHandlerService;
    private MessageHandlingService messageHandlingService;
    private SendMessageService sendMessageService;

    @BeforeEach
    void setUp() {
        messageHandlingService = mock(MessageHandlingService.class);
        sendMessageService = mock(SendMessageService.class);
        updateHandlerService = new UpdateHandlerServiceImpl(messageHandlingService, sendMessageService);
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

        Optional<SendMessage> result = updateHandlerService.handleUpdate(update);
        assertThat(result)
            .isPresent()
            .contains(resultMessage); 
    }

    @Test
    void shouldNotHandleOtherUpdate() {
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(null);

        Optional<SendMessage> result = updateHandlerService.handleUpdate(update);
        assertThat(result)
            .isEmpty();
    }
}
