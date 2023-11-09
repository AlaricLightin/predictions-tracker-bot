package io.github.alariclightin.predictionstrackerbot.commands;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class StartCommand implements MessageHandler {
    private final UserLanguageService userLanguageService;

    StartCommand(UserLanguageService userLanguageService) {
        this.userLanguageService = userLanguageService;
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        var user = message.getUser();
        userLanguageService.setLanguageCode(user.getId(), user.getLanguageCode());

        return new ActionResult(new BotTextMessage("bot.responses.start", message.getUser().getFirstName()));
    }

    @Override
    public String getCommandName() {
        return "start";
    }
    
}
