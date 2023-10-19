package io.github.alariclightin.predictionstrackerbot.schedulers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.bot.BotService;
import io.github.alariclightin.predictionstrackerbot.botservice.SendMessageService;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.QuestionsData;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.SetResultsMessageCreator;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class ReminderSenderImplTest {
    private ReminderSenderImpl reminderSender;
    private SetResultsMessageCreator setResultsMessageCreator;
    private StateHolderService stateHolderService;
    private SendMessageService sendMessageService;
    private BotService botService;

    private static final Long USER_ID = 345L;
    private static final List<Integer> QUESTION_IDS = List.of(100, 200);

    @BeforeEach
    void setUp() {
        setResultsMessageCreator = mock(SetResultsMessageCreator.class);
        stateHolderService = mock(StateHolderService.class);
        sendMessageService = mock(SendMessageService.class);
        botService = mock(BotService.class);
        reminderSender = new ReminderSenderImpl(
            setResultsMessageCreator,
            stateHolderService,
            sendMessageService,
            botService);
    }

    @Test
    void shouldDoNothingIfUserIsDoingSomething() {
        WaitedResponseState state = mock(WaitedResponseState.class);
        when(stateHolderService.getState(USER_ID)).thenReturn(state);

        Optional<Integer> result = reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);
        assertThat(result)
            .isEmpty();

        verify(stateHolderService, never()).saveState(anyLong(), any());
        verify(botService, never()).sendMessage(any());
    }

    @Test
    void shouldDoNothingIfNoCommandCreated() {
        when(stateHolderService.getState(USER_ID)).thenReturn(null);
        when(setResultsMessageCreator.createMessage(QUESTION_IDS)).thenReturn(null);

        Optional<Integer> result = reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);
        assertThat(result)
            .isEmpty();

        verify(stateHolderService, never()).saveState(anyLong(), any());
        verify(botService, never()).sendMessage(any());
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

        SendMessage sendMessage = mock(SendMessage.class);
        when(sendMessageService.create(USER_ID, resultMessage)).thenReturn(sendMessage);

        Optional<Integer> result = reminderSender.sendOneReminderToUser(USER_ID, QUESTION_IDS);
        assertThat(result)
            .contains(QUESTION_IDS.get(0));
            
        verify(stateHolderService).saveState(USER_ID, newState);
        verify(botService).sendMessage(sendMessage);
    }
}
