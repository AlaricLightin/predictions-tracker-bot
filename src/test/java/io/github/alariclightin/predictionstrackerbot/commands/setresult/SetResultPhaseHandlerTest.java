package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.data.predictions.ReminderDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SetResultPhaseHandlerTest {
    private SetResultPhaseHandler setResultPhaseHandler;
    private PredictionsResultDbService predictionsResultDbService;
    private ReminderDbService reminderDbService;

    @BeforeEach
    void setUp() {
        predictionsResultDbService = mock(PredictionsResultDbService.class);
        reminderDbService = mock(ReminderDbService.class);
        setResultPhaseHandler = new SetResultPhaseHandler(predictionsResultDbService, reminderDbService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"yes", "no"})
    void shouldHandleAddResultCommandWhenNoOtherQuestions(String command) throws UnexpectedUserMessageException {
        final int questionId = 11;
        UserMessage message = TestUtils.createMessage(command);
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(questionId, null)));

        ActionResult result = setResultPhaseHandler.handle(message, state);

        ArgumentCaptor<Boolean> resultCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Integer> questionIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(predictionsResultDbService).setResult(questionIdCaptor.capture(), resultCaptor.capture());

        assertThat(resultCaptor.getValue())
            .isEqualTo(command.equals("yes"));

        assertThat(questionIdCaptor.getValue())
            .isEqualTo(questionId);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.result-saved");
        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleSkipCommandWhenNoOtherQuestions() throws UnexpectedUserMessageException {
        final int questionId = 11;
        UserMessage message = TestUtils.createMessage("skip");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(questionId, null)));

        ActionResult result = setResultPhaseHandler.handle(message, state);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.result-skipped");
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
        String firstExpectedMessageId) throws UnexpectedUserMessageException {
        final int questionId = 11;
        final int nextQuestionId = 22;
        final int nextNextQuestionId = 2345;
        UserMessage message = TestUtils.createMessage(command);
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

        ActionResult result = setResultPhaseHandler.handle(message, state);
        assertThat(result.botMessage())
            .asInstanceOf(InstanceOfAssertFactories.type(BotTextMessage.class))
            .extracting(BotTextMessage::textDataList)
            .asList()
            .extracting("messageId")
            .containsExactly(firstExpectedMessageId, "bot.responses.setresults.set-result");

        assertThat(result.botMessage())
            .asInstanceOf(InstanceOfAssertFactories.type(BotTextMessage.class))
            .extracting(textMessage -> textMessage.keyboard().buttons())
            .asList()
            .hasSize(1)
            .element(0)
            .asList()
            .extracting("messageId")
            .containsExactly("bot.buttons.yes", "bot.buttons.no", "bot.buttons.skip", "bot.buttons.skip-all");

        assertThat(result.newState().data())
            .isInstanceOf(QuestionsData.class)
            .extracting(d -> ((QuestionsData) d).question().id())
            .isEqualTo(nextQuestionId);
    }

    @Test
    void shouldHandleSkipAllCommand() throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage("skip_all");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(11, null)));

        ActionResult result = setResultPhaseHandler.handle(message, state);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.result-skipped-all");
        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleInvalidCommand() {
        UserMessage message = TestUtils.createMessage("invalid");
        WaitedResponseState state = new WaitedResponseState(
            setResultPhaseHandler.getCommandName(), 
            setResultPhaseHandler.getPhaseName(),
            new QuestionsData(new ArrayList<>(), TestUtils.createQuestion(11, null)));

        assertThrows(UnexpectedUserMessageException.class, 
            () -> setResultPhaseHandler.handle(message, state));
    }
}
