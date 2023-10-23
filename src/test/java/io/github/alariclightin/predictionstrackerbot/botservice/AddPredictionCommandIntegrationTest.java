package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest

class AddPredictionCommandIntegrationTest extends TestWithContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private TelegramLongPollingBot bot;
    
    @Autowired
    private IncomingMessageGateway incomingMessageGateway;

    @SpyBean
    private OutcomingMessageGateway outcomingMessageGateway;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldAddPrediction() {
        Update update = BotTestUtils.createTextUpdate("/add");        
        incomingMessageGateway.handleUpdate(update);

        assertResponse("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            Update update = BotTestUtils.createTextUpdate("/add");
            incomingMessageGateway.handleUpdate(update);
        }

        @Test
        void shouldHandlePredictionText() {
            Update update = BotTestUtils.createTextUpdate("test prediction");
            incomingMessageGateway.handleUpdate(update);
            
            assertResponse("check the result");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                Update update = BotTestUtils.createTextUpdate("test prediction");
                incomingMessageGateway.handleUpdate(update);
            }

            @Test
            void shouldHandleCorrectDeadlineText() {
                Update update = BotTestUtils.createTextUpdate("2021-01-01");
                incomingMessageGateway.handleUpdate(update);
                
                assertResponse("time");

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
                    incomingMessageGateway.handleUpdate(update);
                }

                @Test
                void shouldHandleCorrectDeadlineTimeText() {
                    Update update = BotTestUtils.createTextUpdate("12:00");
                    incomingMessageGateway.handleUpdate(update);
                    
                    assertResponse("probability");

                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                        .isZero();
                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                        .isZero();
                }

                @Nested
                class WhenDeadLineTimeAdded {
                    @BeforeEach
                    void setUp() {
                        incomingMessageGateway.handleUpdate(BotTestUtils.createTextUpdate("12:00"));
                    }

                    @Test
                    void shouldHandleProbabilityText() {
                        Update update = BotTestUtils.createTextUpdate("60");
                        incomingMessageGateway.handleUpdate(update);
                        
                        assertResponse("Prediction added.", "test prediction", "60");
                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                                .isEqualTo(1);
                        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                                .isEqualTo(1);
                    }

                    @Test
                    void shouldHandleWhenProbabilityIsNotANumber() {
                        Update update = BotTestUtils.createTextUpdate("not a number");
                        incomingMessageGateway.handleUpdate(update);
                        
                        assertResponse("Probability must be a number");
                    }

                    @ParameterizedTest
                    @ValueSource(strings = { "-1", "0", "100" })
                    void shouldHandleWhenProbabilityOutOfRange(String value) {
                        Update update = BotTestUtils.createTextUpdate(value);
                        incomingMessageGateway.handleUpdate(update);

                        assertResponse("Probability must be between 1 and 99");
                    }

                }

                @Test
                void shouldHandleIncorrectDeadlineTimeText() {
                    Update update = BotTestUtils.createTextUpdate("incorrect time");
                    incomingMessageGateway.handleUpdate(update);
                    
                    assertResponse("Wrong time format");
                }
            }

            @Test
            void shouldHandleIncorrectDeadlineText() {
                Update update = BotTestUtils.createTextUpdate("incorrect deadline");
                incomingMessageGateway.handleUpdate(update);
                
                assertResponse("Wrong date format");
            }

        }
    }

    private void assertResponse(CharSequence... expectedFragments) {
                ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(outcomingMessageGateway, atLeastOnce()).sendMessage(response.capture());
        assertThat(response.getValue())
            .hasFieldOrPropertyWithValue("chatId", BotTestUtils.CHAT_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains(expectedFragments);
    }
}
