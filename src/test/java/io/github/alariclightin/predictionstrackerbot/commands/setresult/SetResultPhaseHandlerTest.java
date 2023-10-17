package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SetResultPhaseHandlerTest {
    private SetResultPhaseHandler setResultPhaseHandler;
    private PredictionsResultDbService predictionsResultDbService;

    @BeforeEach
    void setUp() {
        predictionsResultDbService = mock(PredictionsResultDbService.class);
        setResultPhaseHandler = new SetResultPhaseHandler(predictionsResultDbService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"yes", "no"})
    void shouldHandleAddResultCommandWhenNoOtherQuestions(String command) throws UnexpectedMessageException {
        final int questionId = 11;
        Message message = TestUtils.createTestMessage(false, command);
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(questionId, null)));

        MessageHandlingResult result = setResultPhaseHandler.handle(message, state);

        ArgumentCaptor<Boolean> resultCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Integer> questionIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(predictionsResultDbService).setResult(questionIdCaptor.capture(), resultCaptor.capture());

        assertThat(resultCaptor.getValue())
            .isEqualTo(command.equals("yes"));

        assertThat(questionIdCaptor.getValue())
            .isEqualTo(questionId);

        assertThat(result.botMessage())
            .isInstanceOf(BotTextMessage.class)
            .extracting(m -> ((BotTextMessage) m).messageId())
            .isEqualTo("bot.responses.result-saved");

        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleSkipCommandWhenNoOtherQuestions() throws UnexpectedMessageException {
        final int questionId = 11;
        Message message = TestUtils.createTestMessage(false, "skip");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(questionId, null)));

        MessageHandlingResult result = setResultPhaseHandler.handle(message, state);

        assertThat(result.botMessage())
            .isInstanceOf(BotTextMessage.class)
            .extracting(m -> ((BotTextMessage) m).messageId())
            .isEqualTo("bot.responses.result-skipped");

        assertThat(result.newState())
            .isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "yes, bot.responses.result-saved",
        "no, bot.responses.result-saved",
        "skip, bot.responses.result-skipped",
    })
    void shouldHandleCommandsWhenOtherQuestionsExist(
        String command, 
        String firstExpectedMessageId) throws UnexpectedMessageException {
        final int questionId = 11;
        final int nextQuestionId = 22;
        final int nextNextQuestionId = 2345;
        Message message = TestUtils.createTestMessage(false, command);
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(
                new ArrayList<>(List.of(nextQuestionId, nextNextQuestionId)), 
                TestUtils.createQuestion(questionId, null))
            );

        Question nextQuestion = TestUtils.createQuestion(nextQuestionId, null);
        when(predictionsResultDbService.getQuestion(nextQuestionId))
            .thenReturn(nextQuestion);

        MessageHandlingResult result = setResultPhaseHandler.handle(message, state);
        assertThat(result.botMessage())
            .isInstanceOf(BotMessageList.class)
            .extracting(m -> ((BotMessageList) m).botMessages())
            .asList()
            .extracting(m -> ((BotTextMessage) m).messageId())
            .containsExactly(firstExpectedMessageId, "bot.responses.setresults.set-result");

        assertThat(result.newState().data())
            .isInstanceOf(QuestionsData.class)
            .extracting(d -> ((QuestionsData) d).question().id())
            .isEqualTo(nextQuestionId);
    }

    @Test
    void shouldHandleSkipAllCommand() throws UnexpectedMessageException {
        Message message = TestUtils.createTestMessage(false, "skip_all");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(11, null)));

        MessageHandlingResult result = setResultPhaseHandler.handle(message, state);

        assertThat(result.botMessage())
            .isInstanceOf(BotTextMessage.class)
            .extracting(m -> ((BotTextMessage) m).messageId())
            .isEqualTo("bot.responses.result-skipped-all");

        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleInvalidCommand() {
        Message message = TestUtils.createTestMessage(false, "invalid");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(11, null)));

        assertThrows(UnexpectedMessageException.class, () -> setResultPhaseHandler.handle(message, state));
    }
}
