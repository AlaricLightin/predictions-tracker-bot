package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SetResultsCommandHandlerTest {
    private SetResultsCommandHandler setResultsCommandHandler;
    private PredictionsResultDbService predictionsResultDbService;

    @BeforeEach
    void setUp() {
        predictionsResultDbService = mock(PredictionsResultDbService.class);
        setResultsCommandHandler = new SetResultsCommandHandler(predictionsResultDbService);
    }

    @Test
    void shouldHandleCommandIfNoWaitingQuestions() throws UnexpectedMessageException {
        when(predictionsResultDbService.getWaitingQuestionsIds(TestUtils.CHAT_ID)).thenReturn(List.of());
        MessageHandlingResult result = setResultsCommandHandler.handle(
            TestUtils.createTestMessage(true, "/setresults"), null);

        assertThat(result.botMessage())
            .isInstanceOf(BotTextMessage.class)
            .extracting(m -> ((BotTextMessage) m).messageId())
            .isEqualTo("bot.responses.setresults.no-questions-to-set-results");

        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleCommandWithWaitingQuestion() throws UnexpectedMessageException {
        when(predictionsResultDbService.getWaitingQuestionsIds(TestUtils.CHAT_ID)).thenReturn(List.of(1, 2));
        when(predictionsResultDbService.getQuestion(1))
            .thenReturn(TestUtils.createQuestion(1, true));
        when(predictionsResultDbService.getQuestion(2))
            .thenReturn(TestUtils.createQuestion(2, null));
        MessageHandlingResult result = setResultsCommandHandler.handle(
            TestUtils.createTestMessage(true, "/setresults"), null);

        assertThat(result.botMessage())
            .isInstanceOf(BotTextMessage.class)
            .extracting(m -> ((BotTextMessage) m).messageId())
            .isEqualTo("bot.responses.setresults.set-result");

        assertThat(result.newState().data())
            .isInstanceOf(QuestionsData.class)
            .extracting(d -> ((QuestionsData) d).question().id())
            .isEqualTo(2);
    }
}
