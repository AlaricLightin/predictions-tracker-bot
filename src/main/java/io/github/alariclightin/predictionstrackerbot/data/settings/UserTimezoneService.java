package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.time.ZoneId;

public interface UserTimezoneService {
    ZoneId getTimezone(long userId);
    void setTimezone(long userId, String timezone);
}
