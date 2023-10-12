package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.function.BiFunction;
import java.util.function.Function;

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
    private Function<Message, BotMessage> responseMessageFunc;
    private BiFunction<String, T, T> stateUpdater;
    private String nextPhase = null;

    public MessageHandlerBuilder<T> setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }

    public MessageHandlerBuilder<T> setPhaseName(String phaseName) {
        this.phaseName = phaseName;
        return this;
    }

    public MessageHandlerBuilder<T> setNextPhasePromptMessageId(String promptMessageId) {
        this.nextPhasePromptMessageId = promptMessageId;
        return this;
    }

    public MessageHandlerBuilder<T> setStateUpdater(BiFunction<String, T, T> stateUpdater) {
        this.stateUpdater = stateUpdater;
        return this;
    }

    public MessageHandlerBuilder<T> setNextPhase(String nextPhase) {
        this.nextPhase = nextPhase;
        return this;
    }

    public MessageHandlerBuilder<T> setResponseMessageFunc(Function<Message, BotMessage> responseMessageFunc) {
        this.responseMessageFunc = responseMessageFunc;
        return this;
    }

    public MessageHandler build() {
        if (this.commandName == null)
            throw new IllegalStateException("Command name must be set");

        if (this.nextPhasePromptMessageId == null && this.responseMessageFunc == null)
            throw new IllegalStateException("Prompt message id or function must be set");

        Function<Message, BotMessage> promptMessageIdFunc = this.responseMessageFunc != null
            ? this.responseMessageFunc
            : (message) -> new BotTextMessage(this.nextPhasePromptMessageId);    

        BiFunction<String, T, T> stateUpdater = this.stateUpdater != null
            ? this.stateUpdater
            : (text, state) -> null;

        return new SimpleMessageHandler<T>(
            this.commandName,
            this.phaseName,
            promptMessageIdFunc,
            this.nextPhase,
            stateUpdater
        );
    }
    
}
