package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import java.util.Arrays;
import java.util.List;

public record BotKeyboard (
    List<List<InlineButton>> buttons
) {
    
    public static BotKeyboard createOneRowKeyboard(InlineButton... buttons) {
        return new BotKeyboard(List.of(Arrays.asList(buttons)));
    }
}
