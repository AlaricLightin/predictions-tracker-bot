package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat; 
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

class MessageHandlingServiceImplTest {
    private MessageHandlingServiceImpl messageHandlingService;
    private CommandHandlingService commandService;
    private SimpleMessageHandlingService simpleMessageService;

    @BeforeEach
    void setUp() {
        commandService = mock(CommandHandlingService.class);
        simpleMessageService = mock(SimpleMessageHandlingService.class);
        messageHandlingService = new MessageHandlingServiceImpl(commandService, simpleMessageService);
    }

    @Test
    void shouldHandleCommands() {
        Message commandMessage = createTestMessage(true);
        SendMessage mockedResponse = mock(SendMessage.class);
        when(commandService.handle(commandMessage)).thenReturn(mockedResponse);
        
        SendMessage result = messageHandlingService.handlMessage(commandMessage);
        assertThat(result)
            .isEqualTo(mockedResponse);
    }

    @Test
    void shouldHandleOtherMessages() {
        Message simpleMessage = createTestMessage(false);
        SendMessage mockedResponse = mock(SendMessage.class);
        when(simpleMessageService.handle(simpleMessage)).thenReturn(mockedResponse);
        
        SendMessage result = messageHandlingService.handlMessage(simpleMessage);
        assertThat(result)
            .isEqualTo(mockedResponse);
    }

    private Message createTestMessage(boolean isCommand) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(isCommand);
        return message;
    }

}
