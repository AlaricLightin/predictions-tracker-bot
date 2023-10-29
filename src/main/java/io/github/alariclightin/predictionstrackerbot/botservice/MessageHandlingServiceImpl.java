package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedButtonCallbackQueryException;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.HandlersSearchService;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.ButtonCallbackQuery;
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
                MessageHandler.START_PHASE) 
            : stateHolderService.getState(userId);

        return createBotMessageResultAndSaveNewState(state, message);
    }

    private String getCommandName(UserMessage message) {
        return message.getText().substring(1);
    }

    @Override
    public BotMessage handleCallback(ButtonCallbackQuery userCallbackQuery) 
        throws UnexpectedButtonCallbackQueryException {
        
        User user = userCallbackQuery.getUser();
        long userId = user.getId();

        WaitedResponseState state = stateHolderService.getState(userId);
        if (state == null || 
                !state.commandName().equals(userCallbackQuery.getCommand()) ||
                !state.phase().equals(userCallbackQuery.getPhase())) {
                
            throw new UnexpectedButtonCallbackQueryException();
        }

        return createBotMessageResultAndSaveNewState(state, userCallbackQuery);
    }

    private BotMessage createBotMessageResultAndSaveNewState(WaitedResponseState state, UserMessage message) {
        try {
            MessageHandler handler = handlersService.getHandler(state);
            ActionResult result = handler.handle(message, state);
            stateHolderService.saveState(message.getUser().getId(), result.newState());
            return result.botMessage();
        } catch (UnexpectedUserMessageException e) {
            return new BotTextMessage(e.getMessageId(), e.getParameters());
        }
    }

}
