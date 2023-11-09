package io.github.alariclightin.predictionstrackerbot.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class StartCommandTest {
    private StartCommand startCommand;
    private UserLanguageService userLanguageService;

    @BeforeEach
    void setUp() {
        userLanguageService = mock(UserLanguageService.class);
        startCommand = new StartCommand(userLanguageService);
    }

    @Test
    void shouldHandleCommand() throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage("/start");
        
        var result = startCommand.handle(message, null);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.start");
        assertThat(result.newState())
            .isNull();

        verify(userLanguageService).setLanguageCode(TestUtils.CHAT_ID, TestUtils.LANGUAGE_CODE);
    }
}
