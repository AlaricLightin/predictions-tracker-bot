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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.botservice.MessageHandlingService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.integrationutils.AbstractIntegrationTest;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

@SpringBootTest
class SetResultsCommandTest extends AbstractIntegrationTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Bot bot;
    
    @Autowired
    private MessageHandlingService messageHandlingService;

    private static final int WAITINQ_QUESTION_ID_1 = 10;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldRespondAboutAbsentWaitingQuestions() {
        Message message = TestUtils.createTestMessage(true, "/setresults");
        SendMessage response = messageHandlingService.handleMessage(message);

        assertThat(response)
            .extracting(SendMessage::getText)
            .asString()
            .contains("no questions");
    }

    @Test
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRespondToCommandIfWaitingQuestionsExist() {
        Message message = TestUtils.createTestMessage(true, "/setresults");
        SendMessage response = messageHandlingService.handleMessage(message);

        assertThat(response)
            .extracting(SendMessage::getText)
            .asString()
            .contains("Question 1", "yes", "no");
    }

    @Nested
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    class WhenSetResultsStarted {
        
        @BeforeEach
        void setUp() {
            messageHandlingService.handleMessage(TestUtils.createTestMessage(true, "/setresults"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "yes", "no" })
        void shouldHandleSetResultCommand(String command) {
            Message message = TestUtils.createTestMessage(false, command);
            SendMessage response = messageHandlingService.handleMessage(message);

            assertThat(response)
                .extracting(SendMessage::getText)
                .asString()
                .contains("saved", "Question 2", "yes", "no");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isEqualTo(command.equals("yes"));
        }

        @Test
        void voidShouldHandleSkipCommand() {
            Message message = TestUtils.createTestMessage(false, "skip");
            SendMessage response = messageHandlingService.handleMessage(message);

            assertThat(response)
                .extracting(SendMessage::getText)
                .asString()
                .contains("You can add a result later", "Question 2", "yes", "no");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();;
        }

        @Test
        void shouldHandleSkipAllCommand() {
            Message message = TestUtils.createTestMessage(false, "skip_all");
            SendMessage response = messageHandlingService.handleMessage(message);

            assertThat(response)
                .extracting(SendMessage::getText)
                .asString()
                .contains("You can add results later");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();
        }

        @Test
        void shouldHandleInvalidCommand() {
            Message message = TestUtils.createTestMessage(false, "invalid");
            SendMessage response = messageHandlingService.handleMessage(message);

            assertThat(response)
                .extracting(SendMessage::getText)
                .asString()
                .contains("Please, answer \"yes\" or \"no\".");
        }
    }
}
