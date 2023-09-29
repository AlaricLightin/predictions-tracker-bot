package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;

@Service
class MessageHandlingServiceImpl implements MessageHandlingService {
    private final CommandHandlingService commandService;
    private final SimpleMessageHandlingService simpleMessageService;
    private final SendMessageService sendMessageService;

    MessageHandlingServiceImpl(
        CommandHandlingService commandService,
        SimpleMessageHandlingService simpleMessageService,
        SendMessageService sendMessageService) {

        this.commandService = commandService;
        this.simpleMessageService = simpleMessageService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public SendMessage handlMessage(Message message) {
        BotMessage botMessage = message.isCommand() ? commandService.handle(message) 
            : simpleMessageService.handle(message);

        return sendMessageService.create(message.getFrom().getId(), botMessage);
    }

}
