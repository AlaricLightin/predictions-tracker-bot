package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;

@SpringBootTest
class SetResultsCommandIntegrationTest extends AbstractGatewayTest {
    
    private static final int WAITINQ_QUESTION_ID_1 = 10;

    @AfterEach
    void clearTables() {
        clearAllTables();
    }

    @Test
    void shouldRespondAboutAbsentWaitingQuestions() {
        sendTextUpdate("/setresults");

        assertResponseTextContainsFragments("no questions");
    }

    @Test
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRespondToCommandIfWaitingQuestionsExist() {
        sendTextUpdate("/setresults");

        ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(mockedOutcomingGateway, atLeastOnce()).sendMessage(response.capture());
        assertSendMessageContainsFragments(response.getValue(), "Question 1");
        assertSendMessageContainsButtons(response.getValue(), List.of(
            new InlineButton("Yes", "setresults::set-result::YES"),
            new InlineButton("No", "setresults::set-result::NO"),
            new InlineButton("Skip", "setresults::set-result::SKIP"),
            new InlineButton("Skip all", "setresults::set-result::SKIP_ALL")            
        ));
    }

    @Nested
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    class WhenSetResultsStarted {
        
        @BeforeEach
        void setUp() {
            sendTextUpdate("/setresults");
        }

        @ParameterizedTest
        @ValueSource(strings = { "yes", "no" })
        void shouldHandleSetResultCommand(String command) {
            sendTextUpdate(command);

            assertResponseTextContainsFragments("saved", "Question 2");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isEqualTo(command.equals("yes"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "YES", "NO" })
        void shouldHandleButtonCallbackQueryForResultValue(String buttonId) {
            sendCallbackQueryUpdate("setresults", "set-result", buttonId);

            assertResponseTextContainsFragments("saved", "Question 2");
            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isEqualTo(buttonId.equals("YES"));

            assertAnswerCallbackQueryTextIsEmpty();
        }

        @Test
        void voidShouldHandleSkipCommand() {
            sendTextUpdate("skip");

            assertResponseTextContainsFragments("You can add a result later", "Question 2");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();;
        }

        @Test
        void shouldHandleCallbackForSkipButton() {
            sendCallbackQueryUpdate("setresults", "set-result", "SKIP");

            assertResponseTextContainsFragments("You can add a result later", "Question 2");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();

            assertAnswerCallbackQueryTextIsEmpty();
        }

        @Test
        void shouldHandleSkipAllCommand() {
            sendTextUpdate("skip_all");

            assertResponseTextContainsFragments("You can add results later");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();
        }

        @Test
        void shouldHandleCallbackForSkipAllButton() {
            sendCallbackQueryUpdate("setresults", "set-result", "SKIP_ALL");

            assertResponseTextContainsFragments("You can add results later");

            Question savedQuestion = TestDbUtils.getQuestionById(jdbcTemplate, WAITINQ_QUESTION_ID_1);
            assertThat(savedQuestion.result())
                .isNull();

            assertAnswerCallbackQueryTextIsEmpty();
        }

        @Test
        void shouldHandleInvalidCommand() {
            sendTextUpdate("invalid");

            assertResponseTextContainsFragments("Please, answer \"yes\" or \"no\".");
        }
    }

}
