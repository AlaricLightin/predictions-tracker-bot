package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.HandlersSearchService;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class MessageHandlingServiceImpl implements MessageHandlingService {
    private final StateHolderService stateHolderService;
    private final HandlersSearchService handlersService;

    MessageHandlingServiceImpl(
        StateHolderService stateHolderService,
        HandlersSearchService handlersService) {

        this.stateHolderService = stateHolderService;
        this.handlersService = handlersService;
    }

    @Override
    public BotMessage handleTextMessage(UserTextMessage message) {
        User user = message.getUser();
        long userId = user.getId();
        
        WaitedResponseState state = message.isCommand() 
            ? new WaitedResponseState(
                getCommandName(message), 
                MessageHandler.START_PHASE, 
                null) 
            : stateHolderService.getState(userId);

        BotMessage resultBotMessage;
        try {
            MessageHandler handler = handlersService.getHandler(state);
            ActionResult result = handler.handle(message, state);
            stateHolderService.saveState(userId, result.newState());
            resultBotMessage = result.botMessage();
        } catch (UnexpectedUserMessageException e) {
            resultBotMessage = new BotTextMessage(e.getMessageId(), e.getParameters());
        }

        return resultBotMessage;
    }

    private String getCommandName(UserMessage message) {
        return message.getText().substring(1);
    }

}
