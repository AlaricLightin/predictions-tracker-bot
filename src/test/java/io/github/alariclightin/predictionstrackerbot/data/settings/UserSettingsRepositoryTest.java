package io.github.alariclightin.predictionstrackerbot.data.settings;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void deleteData() {
        clearAllTables();
    }

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
}
