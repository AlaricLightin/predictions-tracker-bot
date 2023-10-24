package io.github.alariclightin.predictionstrackerbot.testutils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

public class TestUtils {
    public static final Long CHAT_ID = 123L;
    public static final String LANGUAGE_CODE = "en";

    public static BotTextMessage createTestResponseMessage(String responseId) {
        return new BotTextMessage(responseId);
    }

    public static UserMessage createMessage(String text) {
        var message = mock(UserMessage.class);
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.CHAT_ID);
        when(user.getLanguageCode()).thenReturn(TestUtils.LANGUAGE_CODE);
        when(message.getUser()).thenReturn(user);
        when(message.getDateTime()).thenReturn(Instant.now());

        return message;       
    }

    public static UserTextMessage createTextMessage(String text) {
        var message = mock(UserTextMessage.class);
        when(message.isCommand()).thenReturn(text.charAt(0) == '/');
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(CHAT_ID);
        when(user.getLanguageCode()).thenReturn(LANGUAGE_CODE);
        when(message.getUser()).thenReturn(user);
        when(message.getDateTime()).thenReturn(Instant.now());
        when(message.getChatId()).thenReturn(CHAT_ID);

        return message;       
    }

    public static Question createQuestion(int id, Boolean result) {
        return new Question(id, "test", Instant.now(), TestUtils.CHAT_ID, result);
    }

}
