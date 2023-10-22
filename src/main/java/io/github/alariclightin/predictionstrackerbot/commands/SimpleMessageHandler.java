package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.function.BiFunction;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

class SimpleMessageHandler<T> implements MessageHandler {
    private String commandName;
    private String phaseName = MessageHandler.START_PHASE;
    private BiFunction<UserMessage, T, BotMessage> responseMessageFunc;
    private String nextPhase;
    private StateUpdater<T> stateUpdater;
    private ResultAction<T> resultAction; 
    
    SimpleMessageHandler(
        String commandName,
        String phaseName,
        BiFunction<UserMessage, T, BotMessage> responseMessageFunc,
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
    public ActionResult handle(UserMessage message, WaitedResponseState state) 
        throws UnexpectedUserMessageException {

        @SuppressWarnings("unchecked")
        T data = state != null ? (T) state.data() : null;
        T nextStateData = stateUpdater.apply(message.getText(), data);
        resultAction.apply(message, nextStateData);
        
        WaitedResponseState nextState = null;
        if (nextPhase != null) {
            nextState = new WaitedResponseState(commandName, nextPhase, nextStateData);
        }
        return new ActionResult(responseMessageFunc.apply(message, nextStateData), nextState);
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
