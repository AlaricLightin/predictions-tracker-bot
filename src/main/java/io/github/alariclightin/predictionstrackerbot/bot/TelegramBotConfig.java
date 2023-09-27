package io.github.alariclightin.predictionstrackerbot.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "telegram-bot")
public class TelegramBotConfig {
    private String token;
    private String username;

    @ConstructorBinding
    public TelegramBotConfig(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
