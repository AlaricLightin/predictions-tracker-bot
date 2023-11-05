package io.github.alariclightin.predictionstrackerbot.bot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.alariclightin.predictionstrackerbot.botservice.CommandManagementService;
import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;

@Component
public class Bot extends TelegramLongPollingBot {
    private final TelegramBotConfig config;
    private final IncomingMessageGateway incomingMessageGateway;
    private final CommandManagementService commandManagementService;

    public Bot(TelegramBotConfig config, 
        IncomingMessageGateway incomingMessageGateway,
        CommandManagementService commandManagementService) {
        
        super(config.getToken());
        this.config = config;
        this.incomingMessageGateway = incomingMessageGateway;
        this.commandManagementService = commandManagementService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        incomingMessageGateway.handleUpdate(update);
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

    @ServiceActivator(inputChannel = "outcomingMessagesChannel")
    public void sendMessage(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @ServiceActivator(inputChannel = "outcomingFileMessagesChannel")
    public void sendFile(@Header("chatId") String chatId, @Header("filename") String fileName, @Payload byte[] fileData) {
        try( var stream = new ByteArrayInputStream(fileData) ) {
            execute(SendDocument.builder()
                .chatId(chatId)
                .document(new InputFile(stream, fileName))
                .build()
            );
        } catch (TelegramApiException|IOException e) {
            throw new RuntimeException(e);
        }
    } 

}
