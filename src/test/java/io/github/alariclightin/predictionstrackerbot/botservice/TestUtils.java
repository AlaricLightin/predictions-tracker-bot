package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

class TestUtils {
    static final Long TEST_CHAT_ID = 123L;

    static BotTextMessage createTestResponseMessage(String text) {
        return new BotTextMessage(text);
    }

    static Message createTestMessage(boolean isCommand, String text) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(isCommand);
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.TEST_CHAT_ID);
        when(message.getFrom()).thenReturn(user);

        return message;       
    }
}
