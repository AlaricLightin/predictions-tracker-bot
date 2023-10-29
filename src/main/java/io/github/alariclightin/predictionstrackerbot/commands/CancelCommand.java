package io.github.alariclightin.predictionstrackerbot.commands;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class CancelCommand implements MessageHandler {
    private final StateHolderService stateHolderService;

    CancelCommand(StateHolderService stateHolderService) {
        this.stateHolderService = stateHolderService;
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        WaitedResponseState oldState = stateHolderService.getState(message.getUser().getId());
        if (oldState != null)
            return new ActionResult(
                new BotTextMessage("bot.responses.cancelled", oldState.commandName()),
                null
            );
        else
            return new ActionResult(
                new BotTextMessage("bot.responses.nothing_to_cancel"),
                null
            );
    }

    @Override
    public String getCommandName() {
        return "cancel";
    }

    @Override
    public String getPhaseName() {
        return MessageHandler.START_PHASE;
    }
    
}
