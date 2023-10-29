package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;

class CommandManagementServiceImplTest {
    private static ReloadableResourceBundleMessageSource messageSource;
    private CommandManagementServiceImpl commandManagementService;

    private static final String FIRST_COMMAND = "firstCommand";
    private static final String SECOND_COMMAND = "secondCommand";
    private static final String END_PHASE = "end_phase";

    @BeforeAll
    static void messageSourceSetUp() {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

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
            ),
            messageSource
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
            .extracting("command", "description")
            .containsExactly(
                tuple(FIRST_COMMAND, "First command description"),
                tuple(SECOND_COMMAND, "Second command description")
            );
    }
}
