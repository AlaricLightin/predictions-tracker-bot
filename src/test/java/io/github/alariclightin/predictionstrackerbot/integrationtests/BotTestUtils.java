package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class BotTestUtils {

    public static final Long CHAT_ID = 123L;
    public static final String LANGUAGE_CODE = "en"; 
    public static final String CALLBACK_QUERY_ID = "32547858";
        
    private static User createTelegramUser() {
        var user = mock(User.class);
        when(user.getId()).thenReturn(CHAT_ID);
        when(user.getLanguageCode()).thenReturn(LANGUAGE_CODE);
        return user;
    }
    
    private static Message createTelegramMessage(String text) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(text.charAt(0) == '/');
        when(message.getText()).thenReturn(text);

        var user = createTelegramUser();
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(message.getDate()).thenReturn((int) Instant.now().getEpochSecond());

        return message;       
    }

    public static Update createTextUpdate(String text) {
        var message = createTelegramMessage(text);
        var update = new Update();
        update.setMessage(message);
        return update;
    }

    private static CallbackQuery createCallbackQuery(String data) {
        var callbackQuery = new CallbackQuery();
        var user = createTelegramUser();
        callbackQuery.setId(CALLBACK_QUERY_ID);
        callbackQuery.setFrom(user);
        callbackQuery.setData(data);
        return callbackQuery;
    }

    public static Update createCallbackQueryUpdate(String data) {
        var callbackQuery = createCallbackQuery(data);
        var update = new Update();
        update.setCallbackQuery(callbackQuery);
        return update;
    }

}
