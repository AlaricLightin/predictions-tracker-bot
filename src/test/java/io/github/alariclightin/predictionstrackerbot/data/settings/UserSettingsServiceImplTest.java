package io.github.alariclightin.predictionstrackerbot.data.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSettingsServiceImplTest {
    private UserSettingsServiceImpl userSettingsService;
    private UserSettingsRepository userSettingsRepository;
    private DefaultUserSettings defaultUserSettings;

    @BeforeEach
    void setUp() {
        userSettingsRepository = mock(UserSettingsRepository.class);
        defaultUserSettings = mock(DefaultUserSettings.class);
        userSettingsService = new UserSettingsServiceImpl(userSettingsRepository, defaultUserSettings);
    }

    @Test
    void shouldGetTimezoneIfUserSettingsExists() {
        final String timezone = "Europe/London";
        final long userId = 123L;
        when(userSettingsRepository.getTimezoneByUserId(userId)).thenReturn(Optional.of(timezone));

        var result = userSettingsService.getTimezone(userId);
        assertThat(result)
            .isEqualTo(ZoneId.of(timezone));
    }

    @Test
    void shouldGetDefaultTimezoneIfUserSettingsDoesNotExist() {
        final String timezone = "Europe/London";
        final long userId = 123L;
        when(userSettingsRepository.getTimezoneByUserId(userId)).thenReturn(Optional.empty());
        when(defaultUserSettings.timezone()).thenReturn(timezone);

        var result = userSettingsService.getTimezone(userId);
        assertThat(result)
            .isEqualTo(ZoneId.of(timezone));
    }
}
