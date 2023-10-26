package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserSettingsServiceImpl implements UserTimezoneService {
    private final UserSettingsRepository userSettingsRepository;
    private final DefaultUserSettings defaultUserSettings;

    UserSettingsServiceImpl(
        UserSettingsRepository userSettingsRepository,
        DefaultUserSettings defaultUserSettings) {

        this.userSettingsRepository = userSettingsRepository;
        this.defaultUserSettings = defaultUserSettings;
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneId getTimezone(long userId) {
        return userSettingsRepository
            .getTimezoneByUserId(userId)
            .map(ZoneId::of)
            .orElseGet(() -> ZoneId.of(defaultUserSettings.getTimezone()));
    }

    @Override
    @Transactional
    public void setTimezone(long userId, String timezone) {
        userSettingsRepository.setTimezoneByUserId(userId, timezone);
    }
    
}
