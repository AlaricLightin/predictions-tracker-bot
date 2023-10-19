package io.github.alariclightin.predictionstrackerbot.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

class HandlersSearchServiceImplTest {
    private HandlersSearchServiceImpl handlersSearchService;
    
    private static MessageHandler firstCommandHandler = mock(MessageHandler.class);
    private static MessageHandler secondCommandHandler = mock(MessageHandler.class);
    private static MessageHandler endPhaseOfFirstCommandHandler = mock(MessageHandler.class);

    private static final String END_PHASE = "end_phase";
    
    @BeforeEach
    void setUp() {
        handlersSearchService = new HandlersSearchServiceImpl(
            Map.of(
                "firstCommand", Map.of(
                    MessageHandler.START_PHASE, firstCommandHandler,
                    END_PHASE, endPhaseOfFirstCommandHandler
                ),
                "secondCommand", Map.of(
                    MessageHandler.START_PHASE, secondCommandHandler
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("dataForGetHandlerTest")
    void shouldGetHandler(WaitedResponseState state, MessageHandler expectedHandler) 
        throws UnexpectedUserMessageException {

        MessageHandler actualHandler = handlersSearchService.getHandler(state);
        assertThat(actualHandler)
            .isEqualTo(expectedHandler);
    }

    private static Stream<Arguments> dataForGetHandlerTest() {
        return Stream.of(
            Arguments.of(
                new WaitedResponseState("firstCommand", MessageHandler.START_PHASE, null),
                firstCommandHandler
            ),

            Arguments.of(
                new WaitedResponseState("secondCommand", MessageHandler.START_PHASE, null),
                secondCommandHandler
            ),

            Arguments.of(
                new WaitedResponseState("firstCommand", END_PHASE, null),
                endPhaseOfFirstCommandHandler
            )
        );
    }

    @Test
    void shouldHandleUnexpectedCommand() {
        assertThrows(UnexpectedUserMessageException.class, 
            () -> handlersSearchService.getHandler(
                new WaitedResponseState("unexpectedCommand", MessageHandler.START_PHASE, null)
            ));
    }

    @Test
    void shouldHandleUnexpectedText() {
        assertThrows(UnexpectedUserMessageException.class, 
            () -> handlersSearchService.getHandler(null));
    }
}
