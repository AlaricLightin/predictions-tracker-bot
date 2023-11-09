package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.time.ZoneId;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserSettingsServiceImpl implements 
    UserTimezoneService, 
    UserLanguageService, 
    MessageSettingsService {
        
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
            .orElseGet(() -> ZoneId.of(defaultUserSettings.timezone()));
    }

    @Override
    @Transactional
    public void setTimezone(long userId, String timezone) {
        userSettingsRepository.setTimezoneByUserId(userId, timezone);
    }

    @Override
    @Transactional(readOnly = true)
    public String getLanguageCode(long userId) {
        return userSettingsRepository
            .getLanguageCodeByUserId(userId)
            .orElse("en");
    }

    @Override
    @Transactional
    public void setLanguageCode(long userId, String languageCode) {
        userSettingsRepository.setLanguageCodeByUserId(userId, languageCode);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageSettings getSettings(long userId) {
        var timezone = getTimezone(userId);
        var locale = Locale.forLanguageTag(getLanguageCode(userId));
        return new MessageSettings(locale, timezone);
    }
    
}
