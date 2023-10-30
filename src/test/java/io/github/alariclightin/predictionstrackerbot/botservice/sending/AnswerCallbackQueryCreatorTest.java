package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;
import io.github.alariclightin.predictionstrackerbot.testutils.TestMessageSource;

class AnswerCallbackQueryCreatorTest {
    private static MessageSource messageSource;

    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = TestMessageSource.create();
    }

    @Test
    void shouldCreateAnswerCallbackForEmptyMessage() {
        String callbackQueryId = "callback-query-id";
        var botCallbackAnswer = new BotCallbackAnswer("");

        var creator = new AnswerCallbackQueryCreator(
            messageSource,
            callbackQueryId,
            "en",
            botCallbackAnswer
        );
        var result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("callbackQueryId", callbackQueryId)
            .hasFieldOrPropertyWithValue("text", "");
    }

    @Test
    void shouldCreateAnswerCallbackForNonEmptyMessage() {
        String callbackQueryId = "callback-query-id";
        String messageId = "message.test";
        var botCallbackAnswer = new BotCallbackAnswer(messageId);

        var creator = new AnswerCallbackQueryCreator(
            messageSource,
            callbackQueryId,
            "en",
            botCallbackAnswer
        );
        var result = creator.get();

        assertThat(result)
            .hasFieldOrPropertyWithValue("callbackQueryId", callbackQueryId)
            .hasFieldOrPropertyWithValue("text", "Test");
    }

}
