package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.testutils.TestMessageSource;

class AnswerCallbackQueryCreatorTest {
    private static MessageSource messageSource;
    private UserLanguageService userLanguageService = mock(UserLanguageService.class);

    private static final long USER_ID = 467;

    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = TestMessageSource.create();
    }

    @Test
    void shouldCreateAnswerCallbackForEmptyMessage() {
        when(userLanguageService.getLanguageCode(USER_ID)).thenReturn("en");
        String callbackQueryId = "callback-query-id";
        var botCallbackAnswer = new BotCallbackAnswer("");

        var creator = new AnswerCallbackQueryCreator(
            messageSource,
            userLanguageService,
            callbackQueryId,
            USER_ID,
            botCallbackAnswer
        );
        var result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("callbackQueryId", callbackQueryId)
            .hasFieldOrPropertyWithValue("text", "");
    }

    @Test
    void shouldCreateAnswerCallbackForNonEmptyMessage() {
        when(userLanguageService.getLanguageCode(USER_ID)).thenReturn("en");
        String callbackQueryId = "callback-query-id";
        String messageId = "message.test";
        var botCallbackAnswer = new BotCallbackAnswer(messageId);

        var creator = new AnswerCallbackQueryCreator(
            messageSource,
            userLanguageService,
            callbackQueryId,
            USER_ID,
            botCallbackAnswer
        );
        var result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("callbackQueryId", callbackQueryId)
            .hasFieldOrPropertyWithValue("text", "Test");
    }

}
