package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.commands.CommandProcessor;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;

public class CommandServiceImplTest {
    private CommandServiceImpl commandService;
    private CommandProcessor commandProcessor1;
    private CommandProcessor commandProcessor2;
    private StateHolderService stateHolderService;

    @BeforeEach
    void setUp() {
        commandProcessor1 = mock(CommandProcessor.class);
        when(commandProcessor1.handleCommand(any())).thenReturn(TestUtils.createTestResponseMessage("response1"));
        when(commandProcessor1.createBotCommand()).thenReturn(
            new BotCommand("command1", "description1"));
        commandProcessor2 = mock(CommandProcessor.class);
        when(commandProcessor2.handleCommand(any())).thenReturn(TestUtils.createTestResponseMessage("response2"));
        when(commandProcessor2.createBotCommand()).thenReturn(
            new BotCommand("command2", "description2"));

        stateHolderService = mock(StateHolderService.class);
        
        commandService = new CommandServiceImpl(
                Map.of(
                        "command1", commandProcessor1,
                        "command2", commandProcessor2),
                stateHolderService
        );
    }

    @ParameterizedTest
    @MethodSource("dataForShouldChooseProcessorCorrectly")
    void shouldChooseProcessorCorrectly(
            String command, String responseText) {
        SendMessage result = commandService.handle(
                createTestMessage(command));

        verify(stateHolderService).deleteState(TestUtils.TEST_CHAT_ID);

        assertThat(result)
            .hasFieldOrPropertyWithValue("chatId", TestUtils.TEST_CHAT_ID.toString())
            .hasFieldOrPropertyWithValue("text", responseText);
    }

    private static Stream<Arguments> dataForShouldChooseProcessorCorrectly() {
        return Stream.of(
                Arguments.of("command1", "response1"),
                Arguments.of("command2", "response2"),
                Arguments.of("command3", "command3 is not a valid command"));
    }

    private Message createTestMessage(String command) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn("/" + command);

        var user = mock(User.class);
        when(user.getId()).thenReturn(TestUtils.TEST_CHAT_ID);
        when(message.getFrom()).thenReturn(user);

        return message;
    }

    @Test
    void shouldCreateBotCommandList() {
        var botCommands = commandService.getBotCommands();

        assertThat(botCommands)
                .hasSize(2)
                .anyMatch(command -> command.getCommand().equals("command1"))
                .anyMatch(command -> command.getCommand().equals("command2"));
    }
}
