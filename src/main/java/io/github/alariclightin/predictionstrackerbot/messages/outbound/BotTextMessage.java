package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record BotTextMessage(
    List<TextData> textDataList,
    BotKeyboard keyboard
) implements BotMessage {

    public BotTextMessage {
        Objects.requireNonNull(textDataList);
        assert !textDataList.isEmpty();
    }

    public BotTextMessage(String messageId, Object... messageArgs) {
        this(List.of(new TextData(messageId, messageArgs)), null);
    }

    public BotTextMessage(BotKeyboard keyboard, String messageId, Object... messageArgs) {
        this(List.of(new TextData(messageId, messageArgs)), keyboard);
    }

    public BotTextMessage add(BotTextMessage message) {
        if (this.keyboard != null && message.keyboard != null) {
            throw new IllegalArgumentException("Cannot merge two text messages with keyboards");
        }
        BotKeyboard keyboard = this.keyboard != null ? this.keyboard : message.keyboard;
        return new BotTextMessage(
            Stream.concat(this.textDataList.stream(), message.textDataList.stream()).toList(),
            keyboard
        );
    }

    public static record TextData(
        String messageId,
        Object[] args
    ) {}
}
