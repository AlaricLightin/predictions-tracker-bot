package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.jdbc.JdbcTestUtils;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;

@SpringBootTest
class AddPredictionCommandIntegrationTest extends AbstractGatewayTest {
    
    @MockBean
    private Clock clock;

    @MockBean
    private UserTimezoneService userTimezoneService;

    private static final Instant CURRENT_INSTANT = Instant.parse("2020-01-01T00:00:00Z");;
    private static final String PREDICTION_TEXT = "test prediction";
    private static final String DEADLINE_DATE = "2021-01-01";
    private static final String DEADLINE_TIME = "12:00";
    private static final Instant DEADLINE_INSTANT = Instant.parse("2021-01-01T10:00:00Z");

    @BeforeEach
    void setUpClock() {
        when(clock.instant()).thenReturn(CURRENT_INSTANT);
    }

    @BeforeEach
    void setUpUserTimezoneService() {
        when(userTimezoneService.getTimezone(BotTestUtils.CHAT_ID)).thenReturn(ZoneId.of("Israel"));
    }

    @Test
    void shouldAddPrediction() {
        sendTextUpdate("/" + TestConsts.ADD_PREDICTION_COMMAMD);        

        assertResponseTextContainsFragments("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            sendTextUpdate("/" + TestConsts.ADD_PREDICTION_COMMAMD);
        }

        @Test
        void shouldHandlePredictionText() {
            sendTextUpdate(PREDICTION_TEXT);
            
            assertResponseTextContainsFragments("check the result");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                sendTextUpdate(PREDICTION_TEXT);
            }

            @Test
            void shouldHandleCorrectDateText() {
                sendTextUpdate(DEADLINE_DATE);
                
                assertResponseTextContainsFragments("time");

                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                    .isZero();
                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                    .isZero();
            }

            @Nested
            class WhenDeadlineTextAdded {
                @BeforeEach
                void setUp() {
                    sendTextUpdate(DEADLINE_DATE);
                }

                @Test
                void shouldHandleCorrectDeadlineTimeText() {
                    sendTextUpdate(DEADLINE_TIME);
                    
                    assertResponseTextContainsFragments("probability");

                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                        .isZero();
                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                        .isZero();
                }

                @Nested
                class WhenDeadLineTimeAdded {
                    @BeforeEach
                    void setUp() {
                        sendTextUpdate(DEADLINE_TIME);
                    }

                    @Test
                    void shouldHandleProbabilityText() {
                        sendTextUpdate("60");
                        
                        assertResponseTextContainsFragments("Prediction was added.", "test prediction", "60");

                        assertThat(TestDbUtils.getQuestions(jdbcTemplate))
                            .hasSize(1)
                            .first()
                            .hasFieldOrPropertyWithValue("text", PREDICTION_TEXT)
                            .hasFieldOrPropertyWithValue("deadline", DEADLINE_INSTANT);

                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                                .isEqualTo(1);
                    }

                    @Test
                    void shouldHandleWhenProbabilityIsNotANumber() {
                        sendTextUpdate("not a number");
                        
                        assertResponseTextContainsFragments("Probability must be a number");
                    }

                    @ParameterizedTest
                    @ValueSource(strings = { "-1", "0", "100" })
                    void shouldHandleWhenProbabilityOutOfRange(String value) {
                        sendTextUpdate(value);

                        assertResponseTextContainsFragments("Probability must be between 1 and 99");
                    }

                }

                @Test
                void shouldHandleIncorrectDeadlineTimeText() {
                    sendTextUpdate("incorrect time");
                    
                    assertResponseTextContainsFragments("Wrong time format");
                }
            }

            @Test
            void shouldHandleCorrectDateTimeText() {
                sendTextUpdate(DEADLINE_DATE + " " + DEADLINE_TIME);
                
                assertResponseTextContainsFragments("probability");
            }

            @Test
            void shouldHandleCallbackFromButton() {
                sendButtonCallbackQueryUpdate(
                    TestConsts.ADD_PREDICTION_COMMAMD, "date", "ONE_HOUR");

                assertResponseTextContainsFragments("probability");
            }

            @Nested
            class WhenDeadlineDateTimeAdded {
                @BeforeEach
                void setUp() {
                    sendButtonCallbackQueryUpdate(
                        TestConsts.ADD_PREDICTION_COMMAMD, "date", "ONE_HOUR");
                }

                @Test
                void shouldHandleCorrectProbabilityText() {
                    sendTextUpdate("60");
                    
                    assertResponseTextContainsFragments("Prediction was added.", "test prediction", "60");
                        assertThat(TestDbUtils.getQuestions(jdbcTemplate))
                            .hasSize(1)
                            .first()
                            .hasFieldOrPropertyWithValue("text", PREDICTION_TEXT)
                            .hasFieldOrPropertyWithValue("deadline", CURRENT_INSTANT.plusSeconds(3600));

                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                        .isEqualTo(1);
                }

            }

            @Test
            void shouldHandleIncorrectDeadlineText() {
                sendTextUpdate("incorrect deadline");
                
                assertResponseTextContainsFragments("Wrong date-time format");
            }

            @Test 
            void shouldHandleDeadlineInThePast() {
                sendTextUpdate("2019-01-01");
                
                assertResponseTextContainsFragments("You can't set a time to check the result in the past");
            }

        }
    }

}
