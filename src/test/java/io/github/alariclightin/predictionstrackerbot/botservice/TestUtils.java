package io.github.alariclightin.predictionstrackerbot.botservice;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

class TestUtils {
    static final Long TEST_CHAT_ID = 123L;

    static SendMessage createTestResponseMessage(String text) {
        return SendMessage.builder()
                .chatId(TEST_CHAT_ID)
                .text(text)
                .build();
    }
}
