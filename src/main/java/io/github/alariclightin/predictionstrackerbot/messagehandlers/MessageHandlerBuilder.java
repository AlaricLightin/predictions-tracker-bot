package io.github.alariclightin.predictionstrackerbot.messagehandlers;

import java.util.function.BiFunction;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

/**
 * Builder for {@link MessageHandler} instances.
 * @param <T> type of the state object
 */
public class MessageHandlerBuilder<T> {
    private String commandName;
    private String phaseName = MessageHandler.START_PHASE;
    private BiFunction<UserMessage, T, BotMessage> responseMessageFunc;
    private StateUpdater<T> stateUpdater = (text, state) -> null;
    private String nextPhase;
    private ResultAction<T> resultAction = (userId, state) -> {}; 

    public MessageHandlerBuilder<T> setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }

    public MessageHandlerBuilder<T> setPhaseName(String phaseName) {
        this.phaseName = phaseName;
        return this;
    }

    /**
     * Sets the message id of the prompt message that will be sent to the user
     */
    public MessageHandlerBuilder<T> setNextPhasePromptMessageId(String promptMessageId) {
        this.responseMessageFunc = (message, data) -> new BotTextMessage(promptMessageId);
        return this;
    }

    public MessageHandlerBuilder<T> setNextPhasePromptMessage(BotMessage promptMessage) {
        this.responseMessageFunc = (message, data) -> promptMessage;
        return this;
    }

    public MessageHandlerBuilder<T> setStateUpdater(StateUpdater<T> stateUpdater) {
        this.stateUpdater = stateUpdater;
        return this;
    }

    public MessageHandlerBuilder<T> setNextPhase(String nextPhase) {
        this.nextPhase = nextPhase;
        return this;
    }

    public MessageHandlerBuilder<T> setResponseMessageFunc(BiFunction<UserMessage, T, BotMessage> responseMessageFunc) {
        this.responseMessageFunc = responseMessageFunc;
        return this;
    }

    /**
     * Sets the action that will be executed after the message is handled.
     */
    public MessageHandlerBuilder<T> setResultAction(ResultAction<T> resultAction) {
        this.resultAction = resultAction;
        return this;
    }

    public MessageHandler build() {
        if (commandName == null)
            throw new IllegalStateException("Command name must be set");

        if (responseMessageFunc == null)
            throw new IllegalStateException("Prompt function must be set");

        return new SimpleMessageHandler<T>(
            commandName,
            phaseName,
            responseMessageFunc,
            nextPhase,
            stateUpdater,
            resultAction
        );
    }
    
}
