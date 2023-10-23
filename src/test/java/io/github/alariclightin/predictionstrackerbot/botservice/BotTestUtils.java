package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

class BotTestUtils {

    static final Long CHAT_ID = 123L;
    static final String LANGUAGE_CODE = "en"; 
        
    private static Message createTelegramMessage(String text) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(text.charAt(0) == '/');
        when(message.getText()).thenReturn(text);

        var user = mock(User.class);
        when(user.getId()).thenReturn(CHAT_ID);
        when(user.getLanguageCode()).thenReturn(LANGUAGE_CODE);
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(CHAT_ID);
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
