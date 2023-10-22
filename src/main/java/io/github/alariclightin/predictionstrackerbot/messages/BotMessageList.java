package io.github.alariclightin.predictionstrackerbot.messages;

import java.util.List;
import java.util.stream.Stream;

public class BotMessageList implements BotMessage {
    private final List<BotMessage> botMessages;

    public BotMessageList(List<BotMessage> botMessages) {
        this.botMessages = flatList(botMessages.stream());
    }

    public BotMessageList(BotMessage... botMessages) {
        this.botMessages = flatList(Stream.of(botMessages));
    }

    private List<BotMessage> flatList(Stream<BotMessage> botMessagesStream) {
        return botMessagesStream
                .<BotMessage>flatMap(botMessage -> {
                    if (botMessage instanceof BotMessageList botMessageList)
                        return botMessageList.botMessages().stream();
                    else
                        return Stream.of(botMessage);
                })
                .toList();        
    }

    public List<BotMessage> botMessages() {
        return botMessages;
    }   
}
