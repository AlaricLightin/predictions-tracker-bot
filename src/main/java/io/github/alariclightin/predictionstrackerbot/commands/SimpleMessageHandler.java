package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.function.BiFunction;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

class SimpleMessageHandler<T> implements MessageHandler {
    private String commandName;
    private String phaseName = MessageHandler.START_PHASE;
    private BiFunction<Message, T, BotMessage> responseMessageFunc;
    private String nextPhase;
    private StateUpdater<T> stateUpdater;
    private ResultAction<T> resultAction; 
    
    SimpleMessageHandler(
        String commandName,
        String phaseName,
        BiFunction<Message, T, BotMessage> responseMessageFunc,
        String nextPhase,
        StateUpdater<T> stateUpdater,
        ResultAction<T> resultAction
    ) {
        this.commandName = commandName;
        this.phaseName = phaseName;
        this.responseMessageFunc = responseMessageFunc;
        this.nextPhase = nextPhase;
        this.stateUpdater = stateUpdater;
        this.resultAction = resultAction;
    }


    @Override
    public MessageHandlingResult handle(Message message, WaitedResponseState state) 
        throws UnexpectedMessageException {

        T data = state != null ? (T) state.data() : null;
        T nextStateData = stateUpdater.apply(message.getText(), data);
        resultAction.apply(message, nextStateData);
        
        WaitedResponseState nextState = null;
        if (nextPhase != null) {
            nextState = new WaitedResponseState(commandName, nextPhase, nextStateData);
        }
        return new MessageHandlingResult(responseMessageFunc.apply(message, nextStateData), nextState);
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
