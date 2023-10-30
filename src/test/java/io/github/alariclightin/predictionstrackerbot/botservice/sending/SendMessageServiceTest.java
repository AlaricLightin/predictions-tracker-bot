package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

class SendMessageServiceTest {
    private SendMessageCreatorFactory sendMessageCreatorFactory;
    private SendMessageService sendMessageService;

    @BeforeEach
    void setUp() {
        sendMessageCreatorFactory = mock(SendMessageCreatorFactory.class);
        sendMessageService = new SendMessageService(sendMessageCreatorFactory);
    }

    @Test
    void shouldCreateSendMessage() {
        final long chatId = 456;
        final String languageCode = "en";
        SendMessage sendMessage = mock(SendMessage.class);
        BotMessage botMessage = mock(BotMessage.class);
        when(sendMessageCreatorFactory.getCreator(chatId, languageCode, botMessage))
            .thenReturn(() -> sendMessage);

        var result = sendMessageService.create(chatId, languageCode, botMessage);
        assertThat(result)
            .isEqualTo(sendMessage);
    }
}
