package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class SimpleInputCommandBuilderTest {
    private static final String COMMAND_NAME = "command-name";
    private static final String PROMPT = "prompt";
    private static final String RESULT = "result";

    private BotMessage responseMessage = mock(BotMessage.class);
    
    private InputResultFunction<String> resultFunction = s -> {
        if (s.equals(PROMPT)) {
            return RESULT;
        } else {
            return null;
        }
    };
    
    @SuppressWarnings("unchecked")
    private ResultAction<String> resultAction = mock(ResultAction.class);
    
    private UserMessage userMessage = TestUtils.createMessage(PROMPT);
    
    private BiFunction<UserMessage, String, BotMessage> responseMessageFunc = (message, data) -> {
        if (data.equals(RESULT) && message.equals(userMessage)) {
            return responseMessage;
        } else {
            return null;
        }
    };


    @Test
    void shouldBuildHandlersSequenceWithPromptMessage() throws UnexpectedUserMessageException {
        // given
        BotMessage promptMessage = mock(BotMessage.class);
        SimpleInputCommandBuilder<String> builder = new SimpleInputCommandBuilder<String>(COMMAND_NAME)
            .setPromptMessage(promptMessage)
            .setResponseMessageFunc(responseMessageFunc)
            .setResultFunction(resultFunction)
            .setResultAction(resultAction);

        // when
        HandlersSequence handlersSequence = builder.build();

        // then
        assertCommandAndPhaseNames(handlersSequence);

        MessageHandler promptHandler = handlersSequence.handlers().get(0);
        ActionResult promptPhaseResult = promptHandler.handle(userMessage, null);
        assertThat(promptPhaseResult)
            .hasFieldOrPropertyWithValue("newState", 
                new WaitedResponseState(COMMAND_NAME, SimpleInputCommandBuilder.INPUT_PHASE, null))
            .hasFieldOrPropertyWithValue("botMessage", promptMessage);

        assertForInputHandler(handlersSequence.handlers().get(1));
    }

    @Test
    void shouldBuildHandlersWithPromptId() throws UnexpectedUserMessageException {
        // given
        String promptMessageId = "prompt-message-id";
        SimpleInputCommandBuilder<String> builder = new SimpleInputCommandBuilder<String>(COMMAND_NAME)
            .setPromptMessageId(promptMessageId)
            .setResponseMessageFunc(responseMessageFunc)
            .setResultFunction(resultFunction)
            .setResultAction(resultAction);

        // when
        HandlersSequence handlersSequence = builder.build();

        // then
        assertCommandAndPhaseNames(handlersSequence);

        MessageHandler promptHandler = handlersSequence.handlers().get(0);
        ActionResult promptPhaseResult = promptHandler.handle(userMessage, null);
        assertThat(promptPhaseResult)
            .hasFieldOrPropertyWithValue("newState", 
                new WaitedResponseState(COMMAND_NAME, SimpleInputCommandBuilder.INPUT_PHASE, null));
        BotMessageAssert.assertIsTextBotMessageWithId(promptPhaseResult.botMessage(), promptMessageId);

        assertForInputHandler(handlersSequence.handlers().get(1));
    }

    private void assertCommandAndPhaseNames(HandlersSequence handlersSequence) {
        assertThat(handlersSequence)
            .hasFieldOrPropertyWithValue("commandName", COMMAND_NAME)
            .extracting("handlers")
            .asList()
            .extracting("commandName", "phaseName")
            .containsExactly(
                tuple(COMMAND_NAME, MessageHandler.START_PHASE),
                tuple(COMMAND_NAME, SimpleInputCommandBuilder.INPUT_PHASE)
            );
    }

    private void assertForInputHandler(MessageHandler handler) throws UnexpectedUserMessageException {
        ActionResult inputPhaseResult = handler.handle(userMessage, null);
        assertThat(inputPhaseResult)
            .hasFieldOrPropertyWithValue("newState", null)
            .hasFieldOrPropertyWithValue("botMessage", responseMessage);

        verify(resultAction).apply(userMessage, RESULT);
    }
}
