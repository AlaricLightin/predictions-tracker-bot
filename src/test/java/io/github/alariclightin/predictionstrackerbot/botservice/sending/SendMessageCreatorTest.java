package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import io.github.alariclightin.predictionstrackerbot.data.settings.MessageSettings;
import io.github.alariclightin.predictionstrackerbot.data.settings.MessageSettingsService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;
import io.github.alariclightin.predictionstrackerbot.testutils.TestMessageSource;

class SendMessageCreatorTest {
    private static MessageSource messageSource;
    private MessageSettingsService messageSettingsService = mock(MessageSettingsService.class);

    private static final long USER_ID = 145;
    
    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = TestMessageSource.create();
    }

    @Test
    void shouldCreateSendMessageForTextMessage() {
        setMockSettings("ru", "Europe/Moscow");
        var botTextMessage = new BotTextMessage("message.hello", "Name" );
        
        var creator = new SendMessageCreator(
            messageSource,
            messageSettingsService,
            USER_ID,
            botTextMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", String.valueOf(USER_ID))
            .hasFieldOrPropertyWithValue("text", "Привет, Name!");
    }

    @Test
    void shouldCreateSendMessageForMessageList() {
        setMockSettings("en", "Europe/London");
        var botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        var botTextMessage2 = new BotTextMessage("message.test");

        var creator = new SendMessageCreator(
            messageSource,
            messageSettingsService,
            USER_ID,
            botTextMessage1.add(botTextMessage2)
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", String.valueOf(USER_ID))
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest");
    }

    @Test
    void shouldCreateSendMessageForComplexMessageList() {
        setMockSettings("en", "Europe/London");
        var botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        var botTextMessage2 = new BotTextMessage("message.test");
        var botTextMessage3 = new BotTextMessage("message.hello", "Name 2" );
        
        var creator = new SendMessageCreator(
            messageSource,
            messageSettingsService,
            USER_ID,
            botTextMessage1.add(botTextMessage2).add(botTextMessage3)
        );
        
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", String.valueOf(USER_ID))
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest\n\nHello, Name 2!");
    }

    @Test
    void shouldCreateSendMessageWithKeyboard() {
        setMockSettings("en", "Europe/London");
        var botMessage = new BotTextMessage(
            BotKeyboard.createOneRowKeyboard(
                new InlineButton("button.yes", "command", "phase", "button-yes"),
                new InlineButton("button.no", "command", "phase", "button-no")
            ),
            "message.hello",
            "Name"
        );

        var creator = new SendMessageCreator(
            messageSource,
            messageSettingsService,
            USER_ID,
            botMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", String.valueOf(USER_ID))
            .hasFieldOrPropertyWithValue("text", "Hello, Name!");

        assertThat(result.getReplyMarkup())
            .isInstanceOf(InlineKeyboardMarkup.class)
            .extracting("keyboard")
            .asList()
            .element(0)
            .asList()
            .extracting("text", "callbackData")
            .containsExactly(
                tuple("Yes", "button::command::phase::button-yes"),
                tuple("No", "button::command::phase::button-no")
            );
            
    }

    @Test
    void shouldCreateMessageWithDateTime() {
        setMockSettings("en", "Europe/Moscow");
        var botTextMessage = new BotTextMessage(
            "message.date",
            Instant.parse("2021-01-01T00:00:00Z"));
            
        var creator = new SendMessageCreator(
            messageSource,
            messageSettingsService,
            USER_ID,
            botTextMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", String.valueOf(USER_ID))
            .hasFieldOrPropertyWithValue("text", "Date: 2021-01-01 03:00");
    }

    private void setMockSettings(String language, String timezoneString) {
        var messageSettings = new MessageSettings(
            Locale.forLanguageTag(language),
            ZoneId.of(timezoneString)
        );
        when(messageSettingsService.getSettings(USER_ID)).thenReturn(messageSettings);
    }
}
