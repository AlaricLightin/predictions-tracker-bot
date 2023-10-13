package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.function.BiFunction;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

/**
 * Builder for {@link MessageHandler} instances.
 * @param <T> type of the state object
 */
public class MessageHandlerBuilder<T> {
    private String commandName;
    private String phaseName = MessageHandler.START_PHASE;
    private String nextPhasePromptMessageId;
    private BiFunction<Message, T, BotMessage> responseMessageFunc;
    private StateUpdater<T> stateUpdater;
    private String nextPhase;
    private ResultAction<T> resultAction; 

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
        this.nextPhasePromptMessageId = promptMessageId;
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

    public MessageHandlerBuilder<T> setResponseMessageFunc(BiFunction<Message, T, BotMessage> responseMessageFunc) {
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
        if (this.commandName == null)
            throw new IllegalStateException("Command name must be set");

        if (this.nextPhasePromptMessageId == null && this.responseMessageFunc == null)
            throw new IllegalStateException("Prompt message id or function must be set");

        BiFunction<Message, T, BotMessage> promptMessageIdFunc = this.responseMessageFunc != null
            ? this.responseMessageFunc
            : (message, data) -> new BotTextMessage(this.nextPhasePromptMessageId);    

        StateUpdater<T> stateUpdater = this.stateUpdater != null
            ? this.stateUpdater
            : (text, state) -> null;

        ResultAction<T> resultAction = this.resultAction != null
            ? this.resultAction
            : (userId, state) -> {};

        return new SimpleMessageHandler<T>(
            this.commandName,
            this.phaseName,
            promptMessageIdFunc,
            this.nextPhase,
            stateUpdater,
            resultAction
        );
    }
    
}
