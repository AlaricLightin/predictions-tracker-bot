package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.HandlersSearchService;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class MessageHandlingServiceImpl implements MessageHandlingService {
    private final StateHolderService stateHolderService;
    private final HandlersSearchService handlersService;
    private final SendMessageService sendMessageService;

    MessageHandlingServiceImpl(
        StateHolderService stateHolderService,
        HandlersSearchService handlersService,
        SendMessageService sendMessageService) {

        this.stateHolderService = stateHolderService;
        this.handlersService = handlersService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    @Transactional
    public SendMessage handleMessage(Message message) {
        User user = message.getFrom();
        long userId = user.getId();
        
        // TODO в случае команды этот запрос лишний. Но в этом случае непонятно, нужно ли обновлять состояние. Подумать.
        WaitedResponseState state = stateHolderService.getState(userId);

        BotMessage resultBotMessage;
        try {
            MessageHandler handler = handlersService.getHandler(message, state);
            MessageHandlingResult result = handler.handle(message, state);
            stateHolderService.saveState(userId, result.newState());
            resultBotMessage = result.botMessage();
        } catch (UnexpectedMessageException e) {
            resultBotMessage = new BotTextMessage(e.getMessageId(), e.getParameters());
        }

        return sendMessageService.create(user.getId(), user.getLanguageCode(), resultBotMessage);
    }

}
