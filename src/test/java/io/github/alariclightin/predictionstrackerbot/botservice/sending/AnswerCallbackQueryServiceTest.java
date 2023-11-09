package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;

class AnswerCallbackQueryServiceTest {
    private AnswerCallbackQueryCreatorFactory answerCallbackQueryCreatorFactory;
    private AnswerCallbackQueryService answerCallbackQueryService;

    @BeforeEach
    void setUp() {
        answerCallbackQueryCreatorFactory = mock(AnswerCallbackQueryCreatorFactory.class);
        answerCallbackQueryService = new AnswerCallbackQueryService(answerCallbackQueryCreatorFactory);
    }

    @Test
    void shouldCreateAnswerCallbackQuery() {
        final String callbackQueryId = "callback-query-id";
        final long userId = 456;
        var botCallbackAnswer = mock(BotCallbackAnswer.class);
        var answerCallbackQuery = mock(AnswerCallbackQuery.class);
        when(answerCallbackQueryCreatorFactory.create(callbackQueryId, userId, botCallbackAnswer))
            .thenReturn(() -> answerCallbackQuery);

        var result = answerCallbackQueryService.createAnswerCallbackQuery(
            callbackQueryId, userId, botCallbackAnswer);

        assertThat(result)
            .isEqualTo(answerCallbackQuery);
    }
}
