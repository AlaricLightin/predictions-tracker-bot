package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

class SimpleMessageHandler<T> implements MessageHandler {
    private String commandName;
    private String phaseName = MessageHandler.START_PHASE;
    private Function<Message, BotMessage> responseMessageFunc;
    private String nextPhase;
    private BiFunction<String, T, T> stateUpdater;
    
    SimpleMessageHandler(
        String commandName,
        String phaseName,
        Function<Message, BotMessage> responseMessageFunc,
        String nextPhase,
        BiFunction<String, T, T> stateUpdater
    ) {
        this.commandName = commandName;
        this.phaseName = phaseName;
        this.responseMessageFunc = responseMessageFunc;
        this.nextPhase = nextPhase;
        this.stateUpdater = stateUpdater;
    }


    @Override
    public MessageHandlingResult handle(Message message, WaitedResponseState state) {
        WaitedResponseState nextState = null;
        if (nextPhase != null) {
            T data = state != null ? (T) state.data() : null;
            nextState = new WaitedResponseState(commandName, nextPhase, stateUpdater.apply(message.getText(), data));
        }
        
        return new MessageHandlingResult(responseMessageFunc.apply(message), nextState);
    }


    @Override
    public String getCommandName() {
        return commandName;
    }


    @Override
    public String getPhaseName() {
        return phaseName;
    }
}
