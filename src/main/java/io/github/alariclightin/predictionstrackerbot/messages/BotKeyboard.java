package io.github.alariclightin.predictionstrackerbot.messages;

import java.util.Arrays;
import java.util.List;

public record BotKeyboard (
    List<List<InlineButton>> buttons
) implements BotMessage {
    
    public static BotKeyboard createOneRowKeyboard(InlineButton... buttons) {
        return new BotKeyboard(List.of(Arrays.asList(buttons)));
    }
}
