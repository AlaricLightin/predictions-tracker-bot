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
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

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
    void shouldGetHandler(Message message, WaitedResponseState state, MessageHandler expectedHandler) 
        throws UnexpectedMessageException {

        MessageHandler actualHandler = handlersSearchService.getHandler(message, state);
        assertThat(actualHandler)
            .isEqualTo(expectedHandler);
    }

    private static Stream<Arguments> dataForGetHandlerTest() {
        return Stream.of(
            Arguments.of(
                TestUtils.createTestMessage(true, "/firstCommand"),
                null,
                firstCommandHandler
            ),

            Arguments.of(
                TestUtils.createTestMessage(true, "/secondCommand"),
                null,
                secondCommandHandler
            ),

            Arguments.of(
                TestUtils.createTestMessage(false, "some text"),
                new WaitedResponseState("firstCommand", END_PHASE, null),
                endPhaseOfFirstCommandHandler
            )
        );
    }

    @Test
    void shouldHandleUnexpectedCommand() {
        Message message = TestUtils.createTestMessage(true, "/unexpectedCommand");
        assertThrows(UnexpectedMessageException.class, 
            () -> handlersSearchService.getHandler(message, null));
    }

    @Test
    void shouldHandleUnexpectedText() {
        Message message = TestUtils.createTestMessage(false, "some text");
        assertThrows(UnexpectedMessageException.class, 
            () -> handlersSearchService.getHandler(message, null));
    }
}
