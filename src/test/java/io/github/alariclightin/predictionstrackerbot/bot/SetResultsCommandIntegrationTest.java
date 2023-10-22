package io.github.alariclightin.predictionstrackerbot.bot;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;

@SpringBootTest
class SetResultsCommandIntegrationTest extends TestWithContainer {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Bot bot;
    
    @Autowired
    private UpdateHandlerService updateHandlerService;

    private static final int WAITINQ_QUESTION_ID_1 = 10;

    @AfterEach
    void clearAllTables() {
        clearAllTables(jdbcTemplate);
    }

    @Test
    void shouldRespondAboutAbsentWaitingQuestions() {
        Update update = BotTestUtils.createTextUpdate("/setresults");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

        assertThat(response)
            .get()
            .extracting(SendMessage::getText)
            .asString()
            .contains("no questions");
    }

    @Test
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRespondToCommandIfWaitingQuestionsExist() {
        Update update = BotTestUtils.createTextUpdate("/setresults");
        Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

        assertThat(response)
            .get()
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
            updateHandlerService.handleUpdate(BotTestUtils.createTextUpdate("/setresults"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "yes", "no" })
        void shouldHandleSetResultCommand(String command) {
            Update update = BotTestUtils.createTextUpdate(command);
            Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

            assertThat(response)
                .get()
                .extracting(SendMessage::getText)
                .asString()
                .contains("saved", "Question 2", "yes", "no");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isEqualTo(command.equals("yes"));
        }

        @Test
        void voidShouldHandleSkipCommand() {
            Update update = BotTestUtils.createTextUpdate("skip");
            Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

            assertThat(response)
                .get()
                .extracting(SendMessage::getText)
                .asString()
                .contains("You can add a result later", "Question 2", "yes", "no");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();;
        }

        @Test
        void shouldHandleSkipAllCommand() {
            Update update = BotTestUtils.createTextUpdate("skip_all");
            Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

            assertThat(response)
                .get()
                .extracting(SendMessage::getText)
                .asString()
                .contains("You can add results later");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();
        }

        @Test
        void shouldHandleInvalidCommand() {
            Update update = BotTestUtils.createTextUpdate("invalid");
            Optional<SendMessage> response = updateHandlerService.handleUpdate(update);

            assertThat(response)
                .get()
                .extracting(SendMessage::getText)
                .asString()
                .contains("Please, answer \"yes\" or \"no\".");
        }
    }
}
