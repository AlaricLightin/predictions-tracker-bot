package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotFile;

@Service
class BotFileTransformService {
    
    @Transformer(inputChannel = "botFileChannel", outputChannel = "outcomingFileMessagesChannel")
    public Message<byte[]> transform(@Header("chatId") long chatId, @Payload BotFile botFile) {
        byte[] content = botFile.content();
        return MessageBuilder
            .withPayload(content)
            .setHeader("filename", botFile.filename())
            .setHeader("chatId", chatId)
            .build();
    }
}
