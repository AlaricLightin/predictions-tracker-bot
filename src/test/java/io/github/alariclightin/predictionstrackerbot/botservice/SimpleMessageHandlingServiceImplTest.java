package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.commands.WaitedResponseHandler;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public class SimpleMessageHandlingServiceImplTest {
    private SimpleMessageHandlingServiceImpl simpleMessageHandlingService;
    private WaitedResponseHandler responseHandler1;
    private WaitedResponseHandler responseHandler2;
    private StateHolderService stateHolderService;

    @BeforeEach
    void setUp() {
        responseHandler1 = mock(WaitedResponseHandler.class);
        when(responseHandler1.handleWaitedResponse(any())).thenReturn(TestUtils.createTestResponseMessage("response1"));
        responseHandler2 = mock(WaitedResponseHandler.class);
        when(responseHandler2.handleWaitedResponse(any())).thenReturn(TestUtils.createTestResponseMessage("response2"));

        stateHolderService = mock(StateHolderService.class);
        
        simpleMessageHandlingService = new SimpleMessageHandlingServiceImpl(
                Map.of(
                        "command1", responseHandler1,
                        "command2", responseHandler2),
                stateHolderService
        );
    }

    @ParameterizedTest
    @MethodSource("dataForShouldChooseHandlerCorrectly")
    void shouldChooseHandlerCorrectly(
            String command, String responseText) {

        WaitedResponseState state = new WaitedResponseState(command, "some phase", null);
        when(stateHolderService.getState(TestUtils.TEST_CHAT_ID))
            .thenReturn(state);

        SendMessage result = simpleMessageHandlingService.handle(
                createTestMessage());

        assertThat(result.getText()).isEqualTo(responseText);
    }

    private static Object[][] dataForShouldChooseHandlerCorrectly() {
        return new Object[][] {
            { "command1", "response1" },
            { "command2", "response2" }
        };
    }

    private Message createTestMessage() {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(false);
        when(message.getText()).thenReturn("some text");

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.TEST_CHAT_ID);
        when(message.getFrom()).thenReturn(user);

        return message;       
    }
}
