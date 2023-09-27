package io.github.alariclightin.predictionstrackerbot.botservice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.WaitedResponseHandler;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class SimpleMessageHandlingServiceImpl implements SimpleMessageHandlingService {
    private final Map<String, WaitedResponseHandler> waitedResponseHandlerMap;
    private final StateHolderService stateHolderService;

    SimpleMessageHandlingServiceImpl(
        @Qualifier("waitedResponseHandlerMap") Map<String, WaitedResponseHandler> waitedResponseHandlerMap,
        StateHolderService stateHolderService) {

        this.waitedResponseHandlerMap = waitedResponseHandlerMap;
        this.stateHolderService = stateHolderService;
    }

    @Override
    public SendMessage handle(Message message) {
        long userId = message.getFrom().getId();
        WaitedResponseState state = stateHolderService.getState(userId);
        if (state != null) {
            WaitedResponseHandler handler = waitedResponseHandlerMap.get(state.commandName());
            if (handler != null) {
                return handler.handleWaitedResponse(message);
            }
            else
                throw new IllegalStateException("No handler for command " + state.commandName());
        }
        else
            return SendMessage.builder()
                    .chatId(userId)
                    // TODO fix message
                    .text("I don't understand you.")
                    .build();
    }
    
}
