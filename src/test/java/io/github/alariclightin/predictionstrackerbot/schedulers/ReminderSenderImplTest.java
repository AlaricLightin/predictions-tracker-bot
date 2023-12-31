package io.github.alariclightin.predictionstrackerbot.schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.commands.setresult.QuestionsData;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.SetResultsMessageCreator;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class ReminderSenderImplTest {
    private ReminderSenderImpl reminderSender;
    private SetResultsMessageCreator setResultsMessageCreator;
    private StateHolderService stateHolderService;
    private ReminderGateway reminderGateway;

    private static final Long USER_ID = 345L;
    private static final List<Integer> QUESTION_IDS = List.of(100, 200);

    @BeforeEach
    void setUp() {
        setResultsMessageCreator = mock(SetResultsMessageCreator.class);
        stateHolderService = mock(StateHolderService.class);
        reminderGateway = mock(ReminderGateway.class);
        reminderSender = new ReminderSenderImpl(
            setResultsMessageCreator,
            stateHolderService,
            reminderGateway);
    }

    @Test
    void shouldDoNothingIfUserIsDoingSomething() {
        WaitedResponseState state = mock(WaitedResponseState.class);
        when(stateHolderService.getState(USER_ID)).thenReturn(state);

        reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);

        verify(stateHolderService, never()).saveState(anyLong(), any());
        verify(reminderGateway, never()).sendBotMessage(anyLong(), any());
    }

    @Test
    void shouldDoNothingIfNoCommandCreated() {
        when(stateHolderService.getState(USER_ID)).thenReturn(null);
        when(setResultsMessageCreator.createMessage(QUESTION_IDS)).thenReturn(null);

        reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);

        verify(stateHolderService, never()).saveState(anyLong(), any());
        verify(reminderGateway, never()).sendBotMessage(anyLong(), any());
    }

    @Test
    void shouldSendCommand() {
        when(stateHolderService.getState(USER_ID)).thenReturn(null);
        
        WaitedResponseState newState = mock(WaitedResponseState.class);
        QuestionsData questionsData = new QuestionsData(
            new ArrayList<>(List.of(200)), TestUtils.createQuestion(QUESTION_IDS.get(0), null));
        when(newState.data()).thenReturn(questionsData);
        BotMessage resultMessage = mock(BotMessage.class);
        ActionResult actionResult = new ActionResult(resultMessage, newState);
        when(setResultsMessageCreator.createMessage(QUESTION_IDS)).thenReturn(actionResult);

        reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);
            
        verify(stateHolderService).saveState(USER_ID, newState);
        verify(reminderGateway).sendBotMessage(USER_ID, resultMessage);
    }
}
