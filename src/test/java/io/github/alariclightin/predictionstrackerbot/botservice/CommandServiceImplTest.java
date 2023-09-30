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
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import io.github.alariclightin.predictionstrackerbot.commands.CommandProcessor;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;

public class CommandServiceImplTest {
    private CommandServiceImpl commandService;
    private CommandProcessor commandProcessor1;
    private CommandProcessor commandProcessor2;
    private StateHolderService stateHolderService;

    @BeforeEach
    void setUp() {
        commandProcessor1 = mock(CommandProcessor.class);
        when(commandProcessor1.handleCommand(any())).thenReturn(TestUtils.createTestResponseMessage("responseId1"));
        when(commandProcessor1.createBotCommand()).thenReturn(
            new BotCommand("command1", "description1"));
        commandProcessor2 = mock(CommandProcessor.class);
        when(commandProcessor2.handleCommand(any())).thenReturn(TestUtils.createTestResponseMessage("responseId2"));
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
            String command, String responseId) {
        BotMessage result = commandService.handle(
                TestUtils.createTestMessage(true, "/" + command));

        verify(stateHolderService).deleteState(TestUtils.CHAT_ID);

        assertThat(result)
            .hasFieldOrPropertyWithValue("messageId", responseId);
    }

    private static Stream<Arguments> dataForShouldChooseProcessorCorrectly() {
        return Stream.of(
                Arguments.of("command1", "responseId1"),
                Arguments.of("command2", "responseId2"),
                Arguments.of("command3", "bot.responses.error.unexpected-command"));
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
