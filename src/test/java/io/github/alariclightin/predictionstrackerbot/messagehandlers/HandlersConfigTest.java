package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HandlersConfigTest {
    private HandlersConfig handlersConfig = new HandlersConfig();

    @Test
    void shouldCreateHandlersMap() {
        List<MessageHandler> messageHandlersList = List.of(
            createMockHandler("firstCommand", MessageHandler.START_PHASE),
            createMockHandler("firstCommand", "second_phase"),
            createMockHandler("secondCommand", MessageHandler.START_PHASE)
        );

        List<HandlersSequence> handlersSequences = List.of(
            new HandlersSequence("thirdCommand", List.of(
                createMockHandler("thirdCommand", MessageHandler.START_PHASE),
                createMockHandler("thirdCommand", "second_phase")
            ))
        );

        var handlersMap = handlersConfig.messageHandlersMap(messageHandlersList, handlersSequences);

        assertThat(handlersMap)
            .hasSize(3)
            .containsEntry("firstCommand", Map.of(
                MessageHandler.START_PHASE, messageHandlersList.get(0),
                "second_phase", messageHandlersList.get(1)
            ))
            .containsEntry("secondCommand", Map.of(
                MessageHandler.START_PHASE, messageHandlersList.get(2)
            ))
            .containsEntry("thirdCommand", Map.of(
                MessageHandler.START_PHASE, handlersSequences.get(0).handlers().get(0),
                "second_phase", handlersSequences.get(0).handlers().get(1)
            ));
    }

    private MessageHandler createMockHandler(String commandName, String phaseName) {
        MessageHandler handler = mock(MessageHandler.class);
        when(handler.getCommandName()).thenReturn(commandName);
        when(handler.getPhaseName()).thenReturn(phaseName);
        return handler;
    }
}
