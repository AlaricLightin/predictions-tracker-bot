package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserSettingsServiceImpl implements UserTimezoneService {
    private final UserSettingsRepository userSettingsRepository;

    UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneId getTimezone(long userId) {
        return userSettingsRepository
            .getTimezoneByUserId(userId)
            .map(ZoneId::of)
            .orElse(ZoneId.of("UTC"));
    }

    @Override
    @Transactional
    public void setTimezone(long userId, String timezone) {
        userSettingsRepository.setTimezoneByUserId(userId, timezone);
    }
    
}
