package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.List;

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
import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;
import io.github.alariclightin.predictionstrackerbot.testutils.TestDbUtils;

@SpringBootTest
class SetResultsCommandIntegrationTest extends AbstractGatewayTest {
    
    private static final int WAITINQ_QUESTION_ID_1 = 10;

    @Test
    void shouldRespondAboutAbsentWaitingQuestions() {
        sendTextUpdate("/" + TestConsts.SET_RESULTS_COMMAND);

        assertResponseTextContainsFragments("no questions");
    }

    @Test
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRespondToCommandIfWaitingQuestionsExist() {
        sendTextUpdate("/" + TestConsts.SET_RESULTS_COMMAND);

        ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(mockedOutcomingGateway, atLeastOnce()).sendMessage(response.capture());
        assertSendMessageContainsFragments(response.getValue(), 
            "Question 1",
            "2021-01-01 00:00");
        assertSendMessageContainsButtons(response.getValue(), List.of(
            new ButtonData("Yes", "button::setresults::set-result::YES"),
            new ButtonData("No", "button::setresults::set-result::NO"),
            new ButtonData("Skip", "button::setresults::set-result::SKIP"),
            new ButtonData("Skip all", "button::setresults::set-result::SKIP_ALL")            
        ));
    }

    @Nested
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    class WhenSetResultsStarted {
        
        @BeforeEach
        void setUp() {
            sendTextUpdate("/" + TestConsts.SET_RESULTS_COMMAND);
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
            sendButtonCallbackQueryUpdate(TestConsts.SET_RESULTS_COMMAND, "set-result", buttonId);

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
            sendButtonCallbackQueryUpdate(TestConsts.SET_RESULTS_COMMAND, "set-result", "SKIP");

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
            sendButtonCallbackQueryUpdate(TestConsts.SET_RESULTS_COMMAND, "set-result", "SKIP_ALL");

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
