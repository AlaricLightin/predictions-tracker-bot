package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat; 
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;

class MessageHandlingServiceImplTest {
    private MessageHandlingServiceImpl messageHandlingService;
    private CommandHandlingService commandService;
    private SimpleMessageHandlingService simpleMessageService;
    private SendMessageService sendMessageService;

    private BotMessage botMessage = mock(BotMessage.class);

    @BeforeEach
    void setUp() {
        commandService = mock(CommandHandlingService.class);
        simpleMessageService = mock(SimpleMessageHandlingService.class);
        sendMessageService = mock(SendMessageService.class);
        messageHandlingService = new MessageHandlingServiceImpl(
            commandService, 
            simpleMessageService,
            sendMessageService);
    }

    @Test
    void shouldHandleCommands() {
        Message commandMessage = TestUtils.createTestMessage(true, "/cmd");
        SendMessage mockedResponse = mock(SendMessage.class);
        when(commandService.handle(commandMessage)).thenReturn(botMessage);
        when(sendMessageService.create(TestUtils.CHAT_ID, TestUtils.LANGUAGE_CODE, botMessage))
            .thenReturn(mockedResponse);
        
        SendMessage result = messageHandlingService.handlMessage(commandMessage);
        assertThat(result)
            .isEqualTo(mockedResponse);
    }

    @Test
    void shouldHandleOtherMessages() {
        Message simpleMessage = TestUtils.createTestMessage(false, "some text");
        BotMessage botMessage = mock(BotMessage.class);
        SendMessage mockedResponse = mock(SendMessage.class);
        when(simpleMessageService.handle(simpleMessage)).thenReturn(botMessage);
        when(sendMessageService.create(TestUtils.CHAT_ID, TestUtils.LANGUAGE_CODE, botMessage))
            .thenReturn(mockedResponse);
        
        SendMessage result = messageHandlingService.handlMessage(simpleMessage);
        assertThat(result)
            .isEqualTo(mockedResponse);
    }

}
