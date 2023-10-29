package io.github.alariclightin.predictionstrackerbot.commands.settimezone;

import java.util.Set;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandlerBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Configuration
class SetTimezoneCommandConfig {

    private static final Set<String> TIMEZONES = Set.of(TimeZone.getAvailableIDs());

    @Bean
    MessageHandler setTimezoneCommand() {
        return new MessageHandlerBuilder<String>()
            .setCommandName(SetTimezoneConsts.COMMAND)
            .setNextPhasePromptMessageId("bot.responses.settimezone.ask-timezone")
            .setNextPhase(SetTimezoneConsts.DATA_INPUT_PHASE)
            .setStateUpdater((text, data) -> "")
            .build();
    }
    
    @Bean
    MessageHandler setTimezoneInputPhase(UserTimezoneService timezoneService) {
        return new MessageHandlerBuilder<String>()
            .setCommandName(SetTimezoneConsts.COMMAND)
            .setPhaseName(SetTimezoneConsts.DATA_INPUT_PHASE)

            .setStateUpdater((text, data) -> {
                if (TIMEZONES.contains(text)) {
                    return text;
                }
                else {
                    throw new UnexpectedUserMessageException("bot.responses.error.wrong-timezone");
                }
            })

            .setResponseMessageFunc((message, input) -> 
                new BotTextMessage("bot.responses.settimezone.timezone-is-set", input))

            .setResultAction((message, input) -> 
                timezoneService.setTimezone(message.getUser().getId(), input)
            )
            .build();
    }
}
