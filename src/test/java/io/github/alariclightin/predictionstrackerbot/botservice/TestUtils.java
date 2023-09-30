package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

class TestUtils {
    static final Long CHAT_ID = 123L;
    static final String LANGUAGE_CODE = "en";

    static BotTextMessage createTestResponseMessage(String responseId) {
        return new BotTextMessage(responseId);
    }

    static Message createTestMessage(boolean isCommand, String text) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(isCommand);
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.CHAT_ID);
        when(user.getLanguageCode()).thenReturn(TestUtils.LANGUAGE_CODE);
        when(message.getFrom()).thenReturn(user);

        return message;       
    }
}
