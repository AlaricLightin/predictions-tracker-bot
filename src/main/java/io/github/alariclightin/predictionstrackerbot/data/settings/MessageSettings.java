package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.time.ZoneId;
import java.util.Locale;

public record MessageSettings(
    Locale locale,
    ZoneId timezone
) {

}
