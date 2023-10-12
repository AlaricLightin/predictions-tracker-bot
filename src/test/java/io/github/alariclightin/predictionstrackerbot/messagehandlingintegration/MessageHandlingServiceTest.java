package io.github.alariclightin.predictionstrackerbot.messagehandlingintegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;
import io.github.alariclightin.predictionstrackerbot.integrationutils.AbstractIntegrationTest;

@SpringBootTest
class MessageHandlingServiceTest extends AbstractIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Bot bot;
    
    @Autowired
    private MessageHandlingService messageHandlingService;

    private static final Long USER_ID = 123L;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldHandleStartCommand() {
        Message message = createTestMessage("/start");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", USER_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("Hello");
    }

    @Test
    void shouldAddPrediction() {
        Message message = createTestMessage("/add");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", USER_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("What is your prediction?");
    }

    @Nested
    class WhenAddPredictionStarted {
        @BeforeEach
        void setUp() {
            messageHandlingService.handleMessage(createTestMessage("/add"));
        }

        @Test
        void shouldHandlePredictionText() {
            Message message = createTestMessage("test prediction");
            SendMessage response = messageHandlingService.handleMessage(message);
            assertThat(response.getText())
                .contains("deadline");
        }

        @Nested
        class WhenPredictionTextAdded {
            @BeforeEach
            void setUp() {
                messageHandlingService.handleMessage(createTestMessage("test prediction"));
            }

            @Test
            void shouldHandleCorrectDeadlineText() {
                Message message = createTestMessage("2021-01-01");
                SendMessage response = messageHandlingService.handleMessage(message);
                assertThat(response.getText())
                    .contains("probability");

                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                    .isZero();
                assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                    .isZero();
            }

            @Nested
            class WhenDeadlineTextAdded {
                @BeforeEach
                void setUp() {
                    messageHandlingService.handleMessage(createTestMessage("2021-01-01"));
                }

                @Test
                void shouldHandleProbabilityText() {
                    Message message = createTestMessage("60");
                    SendMessage response = messageHandlingService.handleMessage(message);
                    assertThat(response)
                        .hasFieldOrPropertyWithValue("chatId", USER_ID.toString())
                        .extracting(SendMessage::getText)
                        .asString()
                        .contains("Prediction added.", "test prediction", "60");

                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.questions"))
                        .isEqualTo(1);
                    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "predictions.predictions"))
                        .isEqualTo(1);
                }
            }

            @Test
            void shouldHandleIncorrectDeadlineText() {
                Message message = createTestMessage("incorrect deadline");
                SendMessage response = messageHandlingService.handleMessage(message);
                assertThat(response.getText())
                    .contains("Wrong date");
            }

        }
    }


    @Test
    void shouldHandleUnpredictedTextMessage() {
        Message message = createTestMessage("test message");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", USER_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        Message message = createTestMessage("/invalid");
        SendMessage response = messageHandlingService.handleMessage(message);
        assertThat(response)
            .hasFieldOrPropertyWithValue("chatId", USER_ID.toString())
            .extracting(SendMessage::getText)
            .asString()
            .contains("invalid", "is not a valid command");
    }

    private Message createTestMessage(String string) {
        var message = mock(Message.class);
        when(message.isCommand()).thenReturn(string.charAt(0) == '/');
        when(message.getText()).thenReturn(string);
        
        var user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getLanguageCode()).thenReturn("en");
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(111L);
        when(message.getDate()).thenReturn((int) Instant.now().getEpochSecond());
        return message;
    }
}
