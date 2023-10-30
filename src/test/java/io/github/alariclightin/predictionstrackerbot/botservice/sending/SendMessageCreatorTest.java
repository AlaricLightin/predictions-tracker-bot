package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;
import io.github.alariclightin.predictionstrackerbot.testutils.TestMessageSource;

class SendMessageCreatorTest {
    private static MessageSource messageSource;
    private UserTimezoneService userTimezoneService = mock(UserTimezoneService.class);

    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = TestMessageSource.create();
    }

    @Test
    void shouldCreateSendMessageForTextMessage() {
        var botTextMessage = new BotTextMessage("message.hello", "Name" );
        
        var creator = new SendMessageCreator(
            messageSource,
            userTimezoneService,
            1,
            "ru",
            botTextMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Привет, Name!");
    }

    @Test
    void shouldCreateSendMessageForMessageList() {
        var botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        var botTextMessage2 = new BotTextMessage("message.test");

        var creator = new SendMessageCreator(
            messageSource,
            userTimezoneService,
            1,
            "en",
            new BotMessageList(botTextMessage1, botTextMessage2)
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest");
    }

    @Test
    void shouldCreateSendMessageForComplexMessageList() {
        var botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        var botTextMessage2 = new BotTextMessage("message.test");
        var botTextMessage3 = new BotTextMessage("message.hello", "Name 2" );
        
        var creator = new SendMessageCreator(
            messageSource,
            userTimezoneService,
            1,
            "en",
            new BotMessageList(
                new BotMessageList(botTextMessage1, botTextMessage2),
                botTextMessage3)
        );
        
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest\n\nHello, Name 2!");
    }

    @Test
    void shouldCreateSendMessageWithKeyboard() {
        var botMessage = new BotMessageList(
            new BotTextMessage("message.hello", "Name" ),
            BotKeyboard.createOneRowKeyboard(
                new InlineButton("button.yes", "command", "phase", "button-yes"),
                new InlineButton("button.no", "command", "phase", "button-no")
            )
        );

        var creator = new SendMessageCreator(
            messageSource,
            userTimezoneService,
            1,
            "en",
            botMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
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
        var botTextMessage = new BotTextMessage(
            "message.date",
            Instant.parse("2021-01-01T00:00:00Z"));
            
        when(userTimezoneService.getTimezone(1)).thenReturn(ZoneId.of("Europe/Moscow"));

        var creator = new SendMessageCreator(
            messageSource,
            userTimezoneService,
            1,
            "en",
            botTextMessage
        );
        SendMessage result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Date: 2021-01-01 03:00");
    }

}
