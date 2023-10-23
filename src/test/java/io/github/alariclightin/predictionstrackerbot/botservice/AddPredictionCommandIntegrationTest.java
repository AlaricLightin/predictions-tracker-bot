package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest

class AddPredictionCommandIntegrationTest extends TestWithContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Bot bot;
    
    @Autowired
    private UpdateHandlerService updateHandlerService;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldAddPrediction() {
        Update update = BotTestUtils.createTextUpdate("/add");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
        assertThat(response)
            .get()
            .hasFieldOrPropertyWithValue("chatId", BotTestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            Update update = BotTestUtils.createTextUpdate("/add");
            updateHandlerService.handleUpdate(update);
        }

        @Test
        void shouldHandlePredictionText() {
            Update update = BotTestUtils.createTextUpdate("test prediction");
            Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
            assertThat(response.get().getText())
                .contains("check the result");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                Update update = BotTestUtils.createTextUpdate("test prediction");
                updateHandlerService.handleUpdate(update);
            }

            @Test
            void shouldHandleCorrectDeadlineText() {
                Update update = BotTestUtils.createTextUpdate("2021-01-01");
                Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                assertThat(response.get().getText())
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
                    Update update = BotTestUtils.createTextUpdate("2021-01-01");
                    updateHandlerService.handleUpdate(update);
                }

                @Test
                void shouldHandleCorrectDeadlineTimeText() {
                    Update update = BotTestUtils.createTextUpdate("12:00");
                    Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                    assertThat(response.get().getText())
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
                        updateHandlerService.handleUpdate(BotTestUtils.createTextUpdate("12:00"));
                    }

                    @Test
                    void shouldHandleProbabilityText() {
                        Update update = BotTestUtils.createTextUpdate("60");
                        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                        assertThat(response)
                                .get()
                                .hasFieldOrPropertyWithValue("chatId", BotTestUtils.CHAT_ID.toString())
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
                        Update update = BotTestUtils.createTextUpdate("not a number");
                        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                        assertThat(response.get().getText())
                                .contains("Probability must be a number");
                    }

                    @ParameterizedTest
                    @ValueSource(strings = { "-1", "0", "100" })
                    void shouldHandleWhenProbabilityOutOfRange(String value) {
                        Update update = BotTestUtils.createTextUpdate(value);
                        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                        assertThat(response.get().getText())
                                .contains("Probability must be between 1 and 99");
                    }

                }

                @Test
                void shouldHandleIncorrectDeadlineTimeText() {
                    Update update = BotTestUtils.createTextUpdate("incorrect time");
                    Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                    assertThat(response.get().getText())
                        .contains("Wrong time format");
                }
            }

            @Test
            void shouldHandleIncorrectDeadlineText() {
                Update update = BotTestUtils.createTextUpdate("incorrect deadline");
                Optional<SendMessage> response = updateHandlerService.handleUpdate(update);
                assertThat(response.get().getText())
                    .contains("Wrong date");
            }

        }
    }

}
