package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import io.github.alariclightin.predictionstrackerbot.messages.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.InlineButton;

class SendMessageServiceImplTest {
    private static ReloadableResourceBundleMessageSource messageSource;
    private SendMessageServiceImpl sendMessageService;

    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    @BeforeEach
    void setUp() {
        sendMessageService = new SendMessageServiceImpl(messageSource);
    }

    @Test
    void shouldCreateSendMessageForTextMessage() {
        BotTextMessage botTextMessage = new BotTextMessage("message.hello", "Name" );
        var result = sendMessageService.create(1, "ru", botTextMessage);

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Привет, Name!");
    }

    @Test
    void shouldCreateSendMessageForMessageList() {
        BotTextMessage botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        BotTextMessage botTextMessage2 = new BotTextMessage("message.test");
        var result = sendMessageService.create(1, "en", 
            new BotMessageList(botTextMessage1, botTextMessage2));

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest");
    }

    @Test
    void shouldCreateSendMessageForComplexMessageList() {
        BotTextMessage botTextMessage1 = new BotTextMessage("message.hello", "Name" );
        BotTextMessage botTextMessage2 = new BotTextMessage("message.test");
        BotTextMessage botTextMessage3 = new BotTextMessage("message.hello", "Name 2" );
        
        var result = sendMessageService.create(1, "en",
                new BotMessageList(
                    new BotMessageList(botTextMessage1, botTextMessage2),
                    botTextMessage3)
        );

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", "1")
            .hasFieldOrPropertyWithValue("text", "Hello, Name!\n\nTest\n\nHello, Name 2!");
    }

    @Test
    void shouldCreateSendMessageWithKeyboard() {
        BotMessageList botMessage = new BotMessageList(
            new BotTextMessage("message.hello", "Name" ),
            BotKeyboard.createOneRowKeyboard(
                new InlineButton("button.yes", "button-yes"),
                new InlineButton("button.no", "button-no")
            )
        );

        var result = sendMessageService.create(1, "en", botMessage);

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
                tuple("Yes", "button-yes"),
                tuple("No", "button-no")
            );
            
    }
}
