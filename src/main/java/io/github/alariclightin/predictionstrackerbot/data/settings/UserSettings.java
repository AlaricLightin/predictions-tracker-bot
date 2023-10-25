package io.github.alariclightin.predictionstrackerbot.data.settings;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "user_settings", schema = "users")
record UserSettings(
    @Id
    long userId,

    @Column("timezone")
    String timezone,

    @Column("language_code")
    String languageCode
) {
    
}
