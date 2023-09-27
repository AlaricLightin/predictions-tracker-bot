package io.github.alariclightin.predictionstrackerbot.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
class MessageHandlingServiceImpl implements MessageHandlingService {
    private final CommandHandlingService commandService;
    private final SimpleMessageHandlingService simpleMessageService;

    MessageHandlingServiceImpl(
        CommandHandlingService commandService,
        SimpleMessageHandlingService simpleMessageService) {

        this.commandService = commandService;
        this.simpleMessageService = simpleMessageService;
    }

    @Override
    public SendMessage handlMessage(Message message) {
        if (message.isCommand()) {
            return commandService.handle(message);
        }
        else {
            return simpleMessageService.handle(message);
        }       
    }

}
