package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedButtonCallbackQueryException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

public class UpdateHandlerServiceTest {
    private UpdateHandlerService updateHandlerService;
    private MessageHandlingService messageHandlingService;

    @BeforeEach
    void setUp() {
        messageHandlingService = mock(MessageHandlingService.class);
        updateHandlerService = new UpdateHandlerService(messageHandlingService);
    }

    @Test
    void shouldHandleTextUpdate() {
        UserTextMessage incomingMessage = TestUtils.createTextMessage("test");
        BotMessage botMessage = mock(BotMessage.class);
        when(messageHandlingService.handleTextMessage(incomingMessage)).thenReturn(botMessage);

        var result = updateHandlerService.handleTextMessage(incomingMessage);
        assertThat(result)
            .isEqualTo(botMessage);
    }

    @Test
    void shouldHandleValidCallvackQuery() throws UnexpectedButtonCallbackQueryException {
        ButtonCallbackQuery incomingCallbackQuery = mock(ButtonCallbackQuery.class);
        var botMessage = mock(BotMessage.class);
        when(messageHandlingService.handleCallback(incomingCallbackQuery)).thenReturn(botMessage);

        var result = updateHandlerService.handleCallback(incomingCallbackQuery);
        
        assertThat(result)
            .hasSize(2)
            .usingDefaultComparator()
            .containsExactly(botMessage, new BotCallbackAnswer(""));
    }

    @Test
    void shouldHandleIncorrectCallbackQuery() throws UnexpectedButtonCallbackQueryException {
        ButtonCallbackQuery incomingCallbackQuery = mock(ButtonCallbackQuery.class);
        when(messageHandlingService.handleCallback(incomingCallbackQuery))
            .thenThrow(UnexpectedButtonCallbackQueryException.class);

        var result = updateHandlerService.handleCallback(incomingCallbackQuery);
        assertThat(result.size())
            .isEqualTo(1);

        assertThat(result.get(0))
            .isEqualTo(new BotCallbackAnswer("bot.callback.button-error"));
    }
}
