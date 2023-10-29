package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.jdbc.JdbcTestUtils;

import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;

@SpringBootTest
class SetTimezoneCommandIntegrationTest extends AbstractGatewayTest {
    
    @Test
    void shouldAddPrediction() {
        sendTextUpdate("/" + TestConsts.SET_TIMEZONE_COMMAND);        

        assertResponseTextContainsFragments("timezone");
    }

    @Nested
    class AfterCommandSent {
        
        @BeforeEach
        void setUp() {
            sendTextUpdate("/" + TestConsts.SET_TIMEZONE_COMMAND);
        }

        @Test
        void shouldHandleCorrectTimezone() {
            sendTextUpdate("Europe/Moscow");
            
            assertResponseTextContainsFragments("Now", "Moscow");
            
            assertThat(JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "users.user_settings",
                String.format("user_id = %d AND timezone = 'Europe/Moscow'", BotTestUtils.CHAT_ID)))

                .isEqualTo(1);
        }

        @Test
        void shouldHandleIncorrectTimezone() {
            sendTextUpdate("abraca/dabra");
            
            assertResponseTextContainsFragments("There no such timezone");
        }
    }
}
