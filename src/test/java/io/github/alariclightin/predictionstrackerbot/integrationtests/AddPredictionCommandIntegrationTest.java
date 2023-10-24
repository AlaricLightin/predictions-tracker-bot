package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
class AddPredictionCommandIntegrationTest extends AbstractGatewayTest {

    @AfterEach
    void clearTables() {
        clearAllTables();
    }

    @Test
    void shouldAddPrediction() {
        sendTextUpdate("/add");        

        assertResponseTextContainsFragments("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            sendTextUpdate("/add");
        }

        @Test
        void shouldHandlePredictionText() {
            sendTextUpdate("test prediction");
            
            assertResponseTextContainsFragments("check the result");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                sendTextUpdate("test prediction");
            }

            @Test
            void shouldHandleCorrectDeadlineText() {
                sendTextUpdate("2021-01-01");
                
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
                    sendTextUpdate("2021-01-01");
                }

                @Test
                void shouldHandleCorrectDeadlineTimeText() {
                    sendTextUpdate("12:00");
                    
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
                        sendTextUpdate("12:00");
                    }

                    @Test
                    void shouldHandleProbabilityText() {
                        sendTextUpdate("60");
                        
                        assertResponseTextContainsFragments("Prediction added.", "test prediction", "60");
                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                                .isEqualTo(1);
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
            void shouldHandleIncorrectDeadlineText() {
                sendTextUpdate("incorrect deadline");
                
                assertResponseTextContainsFragments("Wrong date format");
            }

        }
    }

}
