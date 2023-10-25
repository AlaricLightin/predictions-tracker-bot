package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import io.github.alariclightin.predictionstrackerbot.messages.ButtonsConsts;

public record InlineButton(
    String messageId,
    String command,
    String phase,
    String buttonId
) {
    
    public String getCallbackData() {
        return String.join(ButtonsConsts.ID_DELIMITER, 
            ButtonsConsts.BUTTON_PREFIX, command, phase, buttonId);
    }
}
