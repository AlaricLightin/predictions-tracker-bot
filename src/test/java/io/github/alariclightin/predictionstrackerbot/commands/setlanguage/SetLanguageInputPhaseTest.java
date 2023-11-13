package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SetLanguageInputPhaseTest {
    private SetLanguageInputPhase setLanguageInputPhase;
    private UserLanguageService userLanguageService;

    @BeforeEach
    void setUp() {
        userLanguageService = mock(UserLanguageService.class);
        setLanguageInputPhase = new SetLanguageInputPhase(userLanguageService);
    }

    @Test
    void shouldHandleCorrectInput() throws UnexpectedUserMessageException {
        var message = TestUtils.createMessage("ENGLISH");

        var result = setLanguageInputPhase.handle(message, null);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.setlanguage.language-is-set");
        verify(userLanguageService).setLanguageCode(message.getUser().getId(), "en");
    }

    @Test
    void shouldHandleIncorrectInput() {
        var message = TestUtils.createMessage("incorrect");

        UnexpectedUserMessageException exception = assertThrows(UnexpectedUserMessageException.class, 
            () -> setLanguageInputPhase.handle(message, null));
        
        assertThat(exception.getMessageId())
            .isEqualTo("bot.responses.error.wrong-language");
    }
}
