package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import java.util.List;
import java.util.function.BiFunction;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public class SimpleInputCommandBuilder<T> {
    public static final String INPUT_PHASE = "input-phase";

    private final String commandName;
    private BotMessage promptMessage;
    private InputResultFunction<T> resultFunction;
    private ResultAction<T> resultAction = (message, state) -> {};
    private BiFunction<UserMessage, T, BotMessage> responseMessageFunc;

    public SimpleInputCommandBuilder(String commandName) {
        this.commandName = commandName;
    }

    public SimpleInputCommandBuilder<T> setPromptMessage(BotMessage promptMessage) {
        this.promptMessage = promptMessage;
        return this;
    }

    public SimpleInputCommandBuilder<T> setPromptMessageId(String promptMessageId) {
        this.promptMessage = new BotTextMessage(promptMessageId);
        return this;
    }

    public SimpleInputCommandBuilder<T> setResultFunction(InputResultFunction<T> resultFunction) {
        this.resultFunction = resultFunction;
        return this;
    }

    public SimpleInputCommandBuilder<T> setResultAction(ResultAction<T> resultAction) {
        this.resultAction = resultAction;
        return this;
    }

    public SimpleInputCommandBuilder<T> setResponseMessageFunc(
        BiFunction<UserMessage, T, BotMessage> responseMessageFunc) {
        
        this.responseMessageFunc = responseMessageFunc;
        return this;
    }

    public HandlersSequence build() {
        if (promptMessage == null) {
            throw new IllegalStateException("Prompt message is not set");
        }

        MessageHandler promptPhaseHandler = new MessageHandlerBuilder<String>()
            .setCommandName(commandName)
            .setResponseMessageFunc((message, data) -> promptMessage)
            .setNextPhase(INPUT_PHASE)
            .build();

        MessageHandler inputPhaseHandler = new InputPhaseHandler<T>(
            commandName, responseMessageFunc, resultFunction, resultAction);

        return new HandlersSequence(commandName, List.of(promptPhaseHandler, inputPhaseHandler));
    }

    private record InputPhaseHandler<T> (
        String commandName, 
        BiFunction<UserMessage, T, BotMessage> responseMessageFunc,
        InputResultFunction<T> resultFunction,
        ResultAction<T> resultAction
    ) implements MessageHandler {

        @Override
        public ActionResult handle(UserMessage message, WaitedResponseState state)
                throws UnexpectedUserMessageException {
            
            T result = resultFunction.apply(message.getText());
            resultAction.apply(message, result);
            return new ActionResult(responseMessageFunc.apply(message, result));
        }

        @Override
        public String getCommandName() {
            return commandName;
        }

        @Override
        public String getPhaseName() {
            return INPUT_PHASE;
        }
    }

}
