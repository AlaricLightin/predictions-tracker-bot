package io.github.alariclightin.predictionstrackerbot.messagehandlingintegration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class AddPredictionCommandTest extends TestWithContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Bot bot;
    
    @Autowired
    private MessageHandlingService messageHandlingService;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldAddPrediction() {
        Message message = TestUtils.createTestMessage("/add");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            messageHandlingService.handleMessage(TestUtils.createTestMessage("/add"));
        }

        @Test
        void shouldHandlePredictionText() {
            Message message = TestUtils.createTestMessage("test prediction");
            SendMessage response = messageHandlingService.handleMessage(message);
            assertThat(response.getText())
                .contains("check the result");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                messageHandlingService.handleMessage(TestUtils.createTestMessage("test prediction"));
            }

            @Test
            void shouldHandleCorrectDeadlineText() {
                Message message = TestUtils.createTestMessage("2021-01-01");
                SendMessage response = messageHandlingService.handleMessage(message);
                assertThat(response.getText())
                    .contains("time");

                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                    .isZero();
                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                    .isZero();
            }

            @Nested
            class WhenDeadlineTextAdded {
                @BeforeEach
                void setUp() {
                    messageHandlingService.handleMessage(TestUtils.createTestMessage("2021-01-01"));
                }

                @Test
                void shouldHandleCorrectDeadlineTimeText() {
                    Message message = TestUtils.createTestMessage("12:00");
                    SendMessage response = messageHandlingService.handleMessage(message);
                    assertThat(response.getText())
                        .contains("probability");

                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                        .isZero();
                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                        .isZero();
                }

                @Nested
                class WhenDeadLineTimeAdded {
                    @BeforeEach
                    void setUp() {
                        messageHandlingService.handleMessage(TestUtils.createTestMessage("12:00"));
                    }

                    @Test
                    void shouldHandleProbabilityText() {
                        Message message = TestUtils.createTestMessage("60");
                        SendMessage response = messageHandlingService.handleMessage(message);
                        assertThat(response)
                                .hasFieldOrPropertyWithValue("chatId", TestUtils.CHAT_ID.toString())
                                .extracting(SendMessage::getText)
                                .asString()
                                .contains("Prediction added.", "test prediction", "60");

                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                                .isEqualTo(1);
                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                                .isEqualTo(1);
                    }

                    @Test
                    void shouldHandleWhenProbabilityIsNotANumber() {
                        Message message = TestUtils.createTestMessage("not a number");
                        SendMessage response = messageHandlingService.handleMessage(message);
                        assertThat(response.getText())
                                .contains("Probability must be a number");
                    }

                    @ParameterizedTest
                    @ValueSource(strings = { "-1", "0", "100" })
                    void shouldHandleWhenProbabilityOutOfRange() {
                        Message message = TestUtils.createTestMessage("0");
                        SendMessage response = messageHandlingService.handleMessage(message);
                        assertThat(response.getText())
                                .contains("Probability must be between 1 and 99");
                    }

                }

                @Test
                void shouldHandleIncorrectDeadlineTimeText() {
                    Message message = TestUtils.createTestMessage("incorrect time");
                    SendMessage response = messageHandlingService.handleMessage(message);
                    assertThat(response.getText())
                        .contains("Wrong time format");
                }
            }

            @Test
            void shouldHandleIncorrectDeadlineText() {
                Message message = TestUtils.createTestMessage("incorrect deadline");
                SendMessage response = messageHandlingService.handleMessage(message);
                assertThat(response.getText())
                    .contains("Wrong date");
            }

        }
    }

}
