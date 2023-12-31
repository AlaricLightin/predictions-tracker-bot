package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.ReminderDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SetResultsCommandHandlerTest {
    private SetResultsCommandHandler setResultsCommandHandler;
    private PredictionsResultDbService predictionsResultDbService;
    private ReminderDbService reminderDbService;

    @BeforeEach
    void setUp() {
        predictionsResultDbService = mock(PredictionsResultDbService.class);
        reminderDbService = mock(ReminderDbService.class);
        setResultsCommandHandler = new SetResultsCommandHandler(predictionsResultDbService, reminderDbService);
    }

    @Test
    void shouldHandleCommandIfNoWaitingQuestions() throws UnexpectedUserMessageException {
        when(predictionsResultDbService.getWaitingQuestionsIds(TestUtils.CHAT_ID)).thenReturn(List.of());
        ActionResult result = setResultsCommandHandler.handle(
            TestUtils.createTextMessage("/" + TestConsts.SET_RESULTS_COMMAND), null);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.setresults.no-questions-to-set-results");
        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleCommandWithWaitingQuestion() throws UnexpectedUserMessageException {
        when(predictionsResultDbService.getWaitingQuestionsIds(TestUtils.CHAT_ID)).thenReturn(List.of(1, 2));
        when(predictionsResultDbService.getQuestion(1))
            .thenReturn(TestUtils.createQuestion(1, true));
        when(predictionsResultDbService.getQuestion(2))
            .thenReturn(TestUtils.createQuestion(2, null));
        ActionResult result = setResultsCommandHandler.handle(
            TestUtils.createTextMessage("/" + TestConsts.SET_RESULTS_COMMAND), null);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.setresults.set-result");

        assertThat(result.botMessage())
            .extracting("keyboard")
            .isNotNull();

        assertThat(result.newState().data())
            .isInstanceOf(QuestionsData.class)
            .extracting(d -> ((QuestionsData) d).question().id())
            .isEqualTo(2);
    }

    @Test
    void shouldCreateCommandWithWaitingQuestion() {
        when(predictionsResultDbService.getQuestion(1))
            .thenReturn(TestUtils.createQuestion(1, true));
        when(predictionsResultDbService.getQuestion(2))
            .thenReturn(TestUtils.createQuestion(2, null));

        ActionResult result = setResultsCommandHandler.createMessage(List.of(1, 2));

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.setresults.set-result");

        assertThat(result.botMessage())
            .extracting("keyboard")
            .isNotNull();

        assertThat(result.newState().data())
            .isInstanceOf(QuestionsData.class)
            .extracting(d -> ((QuestionsData) d).question().id())
            .isEqualTo(2);
    }

    @Test
    void shouldCreateCommandWithoutWaitingQuestion() {
        when(predictionsResultDbService.getQuestion(1))
            .thenReturn(TestUtils.createQuestion(1, true));
        when(predictionsResultDbService.getQuestion(2))
            .thenReturn(TestUtils.createQuestion(2, false));

        ActionResult result = setResultsCommandHandler.createMessage(List.of(1, 2));
        assertThat(result)
            .isNull();
    }
}
