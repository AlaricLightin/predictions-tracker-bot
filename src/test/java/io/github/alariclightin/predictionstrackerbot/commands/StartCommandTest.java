package io.github.alariclightin.predictionstrackerbot.commands;

import static org.assertj.core.api.Assertions.assertThat; 
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

class StartCommandTest {
    private StartCommand command;

    @BeforeEach
    void setUp() {
        command = new StartCommand();
    }

    @Test
    void shouldRespondToSameChat() {
        final Long chatId = 111L;
        Message message = mock(Message.class);
        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("Test");
        when(user.getId()).thenReturn(chatId);
        when(message.getFrom()).thenReturn(user);

        var result = command.handleCommand(message);
        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", chatId.toString())
            .as("checkText", result.getText().contains("Hello"));
    }
}
