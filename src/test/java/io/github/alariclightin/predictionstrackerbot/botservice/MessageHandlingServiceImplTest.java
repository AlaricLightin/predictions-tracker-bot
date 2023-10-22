package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.HandlersSearchService;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class MessageHandlingServiceImplTest {
    private MessageHandlingServiceImpl messageHandlingService;
    private SendMessageService sendMessageService;
    private StateHolderService stateHolderService;
    private HandlersSearchService handlersService;

    private BotMessage botMessage = mock(BotMessage.class);
    private SendMessage resultMessage = mock(SendMessage.class);


    @BeforeEach
    void setUp() {
        stateHolderService = mock(StateHolderService.class);
        handlersService = mock(HandlersSearchService.class);
        sendMessageService = mock(SendMessageService.class);
        messageHandlingService = new MessageHandlingServiceImpl(
            stateHolderService, handlersService, sendMessageService);
    }

    @Test
    void shouldHandleMessage() throws UnexpectedUserMessageException {
        // given
        UserTextMessage incomingMessage = TestUtils.createTextMessage("test");
        MessageHandler messageHandler = mock(MessageHandler.class);
        WaitedResponseState oldState = mock(WaitedResponseState.class);
        WaitedResponseState newState = mock(WaitedResponseState.class);
        when(stateHolderService.getState(TestUtils.CHAT_ID)).thenReturn(oldState);
        ActionResult handlingResult = new ActionResult(botMessage, newState);
        when(handlersService.getHandler(oldState)).thenReturn(messageHandler);
        when(messageHandler.handle(incomingMessage, oldState)).thenReturn(handlingResult);
        when(sendMessageService.create(TestUtils.CHAT_ID, TestUtils.LANGUAGE_CODE, botMessage))
            .thenReturn(resultMessage);

        // when
        SendMessage result = messageHandlingService.handleMessage(incomingMessage);

        // then
        verify(stateHolderService).saveState(TestUtils.CHAT_ID, newState);

        assertThat(result).isEqualTo(resultMessage);
    }

    @Test
    void shouldHandleCommand() throws UnexpectedUserMessageException {
        //given
        UserTextMessage incomingMessage = TestUtils.createTextMessage("/test");
        MessageHandler messageHandler = mock(MessageHandler.class);
        WaitedResponseState oldState = new WaitedResponseState("test", MessageHandler.START_PHASE, null);
        WaitedResponseState newState = mock(WaitedResponseState.class);
        when(handlersService.getHandler(eq(oldState)))
            .thenReturn(messageHandler);
        ActionResult handlingResult = new ActionResult(botMessage, newState);
        when(messageHandler.handle(eq(incomingMessage), eq(oldState))).thenReturn(handlingResult);
        when(sendMessageService.create(eq(TestUtils.CHAT_ID), eq(TestUtils.LANGUAGE_CODE), any()))
            .thenReturn(resultMessage);
        
        // when
        SendMessage result = messageHandlingService.handleMessage(incomingMessage);

        // then
        verify(stateHolderService).saveState(TestUtils.CHAT_ID, newState);

        assertThat(result).isEqualTo(resultMessage);
    }

    @Test
    void shouldHandleUnexpectedMessages() throws UnexpectedUserMessageException {
        // given
        UserTextMessage incomingMessage = TestUtils.createTextMessage("test");
        WaitedResponseState oldState = mock(WaitedResponseState.class);
        when(stateHolderService.getState(TestUtils.CHAT_ID)).thenReturn(oldState);
        when(handlersService.getHandler(oldState)).thenThrow(new UnexpectedUserMessageException("test"));
        when(sendMessageService.create(eq(TestUtils.CHAT_ID), eq(TestUtils.LANGUAGE_CODE), any()))
            .thenReturn(resultMessage);

        // when
        SendMessage result = messageHandlingService.handleMessage(incomingMessage);

        // then
        verify(stateHolderService, never()).saveState(anyLong(), any());

        assertThat(result).isEqualTo(resultMessage);
    }
}