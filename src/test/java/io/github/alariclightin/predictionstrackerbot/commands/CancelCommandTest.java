package io.github.alariclightin.predictionstrackerbot.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

public class CancelCommandTest {
    private CancelCommand cancelCommand;
    private StateHolderService stateHolderService;

    @BeforeEach
    void setUp() {
        stateHolderService = mock(StateHolderService.class);
        cancelCommand = new CancelCommand(stateHolderService);
    }

    @Test
    void shouldReturnCorrectMessageWhenStateExists() throws UnexpectedUserMessageException {
        WaitedResponseState state = new WaitedResponseState(
            "commandName", "phaseName", null);
        when(stateHolderService.getState(TestUtils.CHAT_ID)).thenReturn(state);

        ActionResult result = cancelCommand.handle(
            TestUtils.createTextMessage("/cancel"), null);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new ActionResult(
                new BotTextMessage("bot.responses.cancelled", "commandName"),
                null
            ));
    }

    @Test
    void shouldReturnCorrectMessageWhenStateDoesNotExist() throws UnexpectedUserMessageException {
        when(stateHolderService.getState(TestUtils.CHAT_ID)).thenReturn(null);

        ActionResult result = cancelCommand.handle(
            TestUtils.createTextMessage("/cancel"), null);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new ActionResult(
                new BotTextMessage("bot.responses.nothing_to_cancel"),
                null
            ));
    }
}
