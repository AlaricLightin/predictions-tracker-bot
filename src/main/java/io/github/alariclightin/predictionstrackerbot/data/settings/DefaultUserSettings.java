package io.github.alariclightin.predictionstrackerbot.data.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "default-user-settings")
record DefaultUserSettings(@DefaultValue("UTC") String timezone) {
}
