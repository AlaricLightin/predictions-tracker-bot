package io.github.alariclightin.predictionstrackerbot.bot;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.alariclightin.predictionstrackerbot.botservice.CommandManagementService;
import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;

@Component
public class Bot extends TelegramLongPollingBot implements BotService {
    private final TelegramBotConfig config;
    private final MessageHandlingService messageHandlingService;
    private final CommandManagementService commandManagementService;

    public Bot(TelegramBotConfig config, 
        MessageHandlingService service,
        CommandManagementService commandManagementService) {
        
        super(config.getToken());
        this.config = config;
        this.messageHandlingService = service;
        this.commandManagementService = commandManagementService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message incomingMessage = update.getMessage();
        if (incomingMessage == null) {
            return;
        }
        
        SendMessage message = messageHandlingService.handleMessage(incomingMessage);
        sendMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }
    
    public boolean setCommands() {
        List<BotCommand> commands = commandManagementService.getBotCommands();
        try {
            return sendApiMethod(new SetMyCommands(commands, null, null));
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
