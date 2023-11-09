package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import io.github.alariclightin.predictionstrackerbot.messages.ButtonsConsts;

public interface CommandInlineButton extends InlineButton {
    String command();
    String phase();
    String buttonId();

    default String getCallbackData() {
        return String.join(ButtonsConsts.ID_DELIMITER, 
            ButtonsConsts.BUTTON_PREFIX, command(), phase(), buttonId());
    }
}
