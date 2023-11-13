package io.github.alariclightin.predictionstrackerbot.commands.setlanguage;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.MessageSourceArgument;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class SetLanguageInputPhase implements MessageHandler {
    private final UserLanguageService userLanguageService;

    SetLanguageInputPhase(UserLanguageService userLanguageService) {
        this.userLanguageService = userLanguageService;
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        LanguageAction command;
        try {
            command = LanguageAction.valueOf(message.getText().toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new UnexpectedUserMessageException("bot.responses.error.wrong-language");
        }

        userLanguageService.setLanguageCode(message.getUser().getId(), command.languageCode());

        return new ActionResult(
            new BotTextMessage("bot.responses.setlanguage.language-is-set", 
                new MessageSourceArgument(command.messageId())
            )
        );

    }

    @Override
    public String getCommandName() {
        return SetLanguageConsts.COMMAND;
    }

    @Override
    public String getPhaseName() {
        return SetLanguageConsts.DATA_INPUT_PHASE;
    }
    
}
