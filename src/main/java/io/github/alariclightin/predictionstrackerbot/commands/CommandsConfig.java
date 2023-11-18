package io.github.alariclightin.predictionstrackerbot.commands;

import java.util.Set;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.HandlersSequence;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.SimpleInputCommandBuilder;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;

@Configuration
class CommandsConfig {

    private static final Set<String> TIMEZONES = Set.of(TimeZone.getAvailableIDs()); 

    @Bean
    HandlersSequence setTimezoneCommandSequence(UserTimezoneService timezoneService) {
        return new SimpleInputCommandBuilder<String>("settimezone")
            .setPromptMessageId("bot.responses.settimezone.ask-timezone")
            
            .setResultFunction(text -> {
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
                timezoneService.setTimezone(message.getUser().getId(), input))
            
            .build();
    }

}
