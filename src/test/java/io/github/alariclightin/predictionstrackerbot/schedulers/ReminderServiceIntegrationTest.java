package io.github.alariclightin.predictionstrackerbot.schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.botservice.BotTestUtils;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
// TODO remove when state will be saved in db
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReminderServiceIntegrationTest extends TestWithContainer {
    @MockBean
    private TelegramLongPollingBot bot;

    @Autowired
    private IncomingMessageGateway incomingMessageGateway;

    @SpyBean
    private OutcomingMessageGateway outcomingMessageGateway;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int WAITINQ_QUESTION_ID_1 = 10;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    @Sql("classpath:sql/waiting-question-ids.sql")
    void shouldUpdateReminders() {
        reminderService.updateReminders();
        
        assertThat(TestDbUtils.getReminders(jdbcTemplate))
                .extracting(TestDbUtils.Reminder::questionId)
                .containsExactly(10, 20, 30);
    }

    @Nested
    class AfterReminderUpdate {
        
        @BeforeEach
        void updateReminders() {
            reminderService.updateReminders();
        }

        @Test
        @Sql("classpath:sql/waiting-question-ids.sql")
        void shouldSendReminders() {
            reminderService.sendReminders();

            ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(outcomingMessageGateway, times(2)).sendMessage(captor.capture());

            List<SendMessage> capturedMessages = captor.getAllValues();
            assertThat(capturedMessages)
                .extracting("chatId")
                .containsExactlyInAnyOrder("123", "225");

            assertThat(capturedMessages)
                .extracting("text")
                .anySatisfy(text -> assertThat(text).asString().contains("Question 1"))
                .anySatisfy(text -> assertThat(text).asString().contains("Question 3"));

                        
            assertThat(TestDbUtils.getReminders(jdbcTemplate))
                .hasSize(3)
                .filteredOn(reminder -> reminder.isSent() == true)
                .hasSize(2);
        }

        @Nested
        class AfterReminderSended {

            @BeforeEach
            void sendReminders() {
                reminderService.sendReminders();
            }

            @Test
            @Sql("classpath:sql/waiting-question-ids.sql")
            void shouldHandleYesCommand() {
                incomingMessageGateway.handleUpdate(BotTestUtils.createTextUpdate("yes"));

                Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
                assertThat(savedQuestion.result())
                    .isTrue();

                List<TestDbUtils.Reminder> reminders = TestDbUtils.getReminders(jdbcTemplate);
                assertThat(reminders)
                    .hasSize(2)
                    .filteredOn(reminder -> reminder.isSent() == true)
                    .hasSize(2);
            }
        }
    }
}
