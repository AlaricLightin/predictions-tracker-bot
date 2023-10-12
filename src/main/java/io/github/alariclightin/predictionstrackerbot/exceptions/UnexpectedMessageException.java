package io.github.alariclightin.predictionstrackerbot.exceptions;

public class UnexpectedMessageException extends Exception {
    private final String messageId;
    private final Object[] parameters;

    public UnexpectedMessageException(String messageId, Object... parameters) {
        this.messageId = messageId;
        this.parameters = parameters;
    }

    public String getMessageId() {
        return messageId;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
