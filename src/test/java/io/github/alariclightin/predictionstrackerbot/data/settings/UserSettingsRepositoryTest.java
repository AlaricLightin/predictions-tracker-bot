package io.github.alariclightin.predictionstrackerbot.data.settings;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSettingsRepositoryTest extends TestWithContainer {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;

    private static final Long USER_ID = 345L;
    private static final String TIMEZONE = "Europe/Paris";

    @Test
    @Sql(statements = {"INSERT INTO users.user_settings (user_id, timezone) VALUES (345, 'Europe/Paris')"})
    void shouldGetTimezoneByUserIdWhenItIsSet() {
        var result = userSettingsRepository.getTimezoneByUserId(USER_ID);
        
        assertThat(result)
            .isPresent()
            .hasValue(TIMEZONE);
    }

    @Test
    void shouldGetEmptyTimezoneByUserIdWhenItIsNotSet() {
        var result = userSettingsRepository.getTimezoneByUserId(USER_ID);
        
        assertThat(result)
            .isEmpty();
    }

    @Test
    void shouldSetTimezoneByUserIdIfSettingsNotExist() {
        userSettingsRepository.setTimezoneByUserId(USER_ID, TIMEZONE);
        
        var result = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users.user_settings", "user_id = 345 AND timezone = 'Europe/Paris'");
        
        assertThat(result)
            .isEqualTo(1);
    }

    @Test
    @Sql(statements = {"INSERT INTO users.user_settings (user_id, timezone) VALUES (345, 'Europe/London')"})
    void shouldSetTimezoneByUserIdIfSettingsExist() {
        userSettingsRepository.setTimezoneByUserId(USER_ID, TIMEZONE);
        
        var result = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users.user_settings", "user_id = 345 AND timezone = 'Europe/Paris'");
        
        assertThat(result)
            .isEqualTo(1);
    }

    @Test
    void shouldSetLanguageCodeByUserIdIfSettingsNotExist() {
        userSettingsRepository.setLanguageCodeByUserId(USER_ID, "en");
        
        var result = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users.user_settings",
            "user_id = 345 AND language_code = 'en'");
        
        assertThat(result)
            .isEqualTo(1);
    }

    @Test
    @Sql(statements = {"INSERT INTO users.user_settings (user_id, language_code) VALUES (345, 'ru')"})
    void shouldSetLanguageCodeByUserIfSettingsExist() {
        userSettingsRepository.setLanguageCodeByUserId(USER_ID, "en");
        
        var result = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users.user_settings", 
            "user_id = 345 AND language_code = 'en'");
        
        assertThat(result)
            .isEqualTo(1);
    }

    @Test
    @Sql(statements = {"INSERT INTO users.user_settings (user_id, language_code) VALUES (345, 'ru')"})
    void shouldGetLanguageCodeByUserIdWhenItIsSet() {
        var result = userSettingsRepository.getLanguageCodeByUserId(USER_ID);
        
        assertThat(result)
            .isPresent()
            .hasValue("ru");
    }

    @Test
    void shouldGetEmptyLanguageCodeByUserIdWhenItIsNotSet() {
        var result = userSettingsRepository.getLanguageCodeByUserId(USER_ID);
        
        assertThat(result)
            .isEmpty();
    }

}
