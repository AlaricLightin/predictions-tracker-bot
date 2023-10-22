package io.github.alariclightin.predictionstrackerbot.bot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class BotTestUtils {
        
    private static Message createTelegramMessage(String text) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(text.charAt(0) == '/');
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.CHAT_ID);
        when(user.getLanguageCode()).thenReturn(TestUtils.LANGUAGE_CODE);
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(TestUtils.CHAT_ID);
        when(message.getDate()).thenReturn((int) Instant.now().getEpochSecond());

        return message;       
    }

    static Update createTextUpdate(String text) {
        var message = createTelegramMessage(text);
        var update = mock(Update.class);
        when(update.getMessage()).thenReturn(message);
        return update;
    }

}
