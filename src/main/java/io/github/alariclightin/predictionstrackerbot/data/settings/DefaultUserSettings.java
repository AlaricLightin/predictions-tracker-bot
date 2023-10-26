package io.github.alariclightin.predictionstrackerbot.data.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "default-user-settings")
public class DefaultUserSettings {
    private String timezone;

    @ConstructorBinding
    public DefaultUserSettings(String timezone) {
        this.timezone = timezone;
    }
    
    public String getTimezone() {
        return timezone;
    }
}
