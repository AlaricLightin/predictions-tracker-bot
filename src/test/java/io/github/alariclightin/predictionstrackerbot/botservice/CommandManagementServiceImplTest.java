package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;

class CommandManagementServiceImplTest {
    private CommandManagementServiceImpl commandManagementService;

    private static final String FIRST_COMMAND = "firstCommand";
    private static final String SECOND_COMMAND = "secondCommand";
    private static final String END_PHASE = "end_phase";

    @BeforeEach
    void setUp() {
        MessageHandler firstCommandHandler = createCommandHandler(FIRST_COMMAND, MessageHandler.START_PHASE);
        MessageHandler endPhaseOfFirstCommandHandler = createCommandHandler(FIRST_COMMAND, END_PHASE);
        MessageHandler secondCommandHandler = createCommandHandler(SECOND_COMMAND, MessageHandler.START_PHASE);

        commandManagementService = new CommandManagementServiceImpl(
            Map.of(
                FIRST_COMMAND, Map.of(
                    MessageHandler.START_PHASE, firstCommandHandler,
                    END_PHASE, endPhaseOfFirstCommandHandler
                ),
                SECOND_COMMAND, Map.of(
                    MessageHandler.START_PHASE, secondCommandHandler
                )
            )
        );
    }

    private MessageHandler createCommandHandler(String commandName, String phaseName) {
        MessageHandler result = mock(MessageHandler.class);
        when(result.getCommandName()).thenReturn(commandName);
        when(result.getPhaseName()).thenReturn(phaseName);
        return result;
    }

    @Test
    void shouldCorrectGetBotCommandList() {
        assertThat(commandManagementService.getBotCommands())
            .extracting("command")
            .containsExactlyInAnyOrder(FIRST_COMMAND, SECOND_COMMAND);
    }
}
