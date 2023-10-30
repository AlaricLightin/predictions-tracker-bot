package io.github.alariclightin.predictionstrackerbot.data.predictions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.jdbc.Sql;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReminderDaoImplTest extends TestWithContainer {
    @Autowired
    private NamedParameterJdbcOperations jdbc;

    private ReminderDaoImpl reminderDao;

    @BeforeEach
    void setUp() {
        reminderDao = new ReminderDaoImpl(jdbc);
    }

    @Test
    @Sql(scripts = {"classpath:sql/waiting-question-ids.sql", "classpath:sql/reminders.sql"})
    void shouldUpdateReminders() {
        reminderDao.updateReminders();

        List<Integer> result = jdbc.queryForList(
            "SELECT question_id FROM predictions.reminders",
            Map.of(),
            Integer.class
        );

        assertThat(result)
            .containsExactlyInAnyOrder(10, 20, 30);
    }

    @ParameterizedTest
    @MethodSource("dataForGetNonSendedReminders")
    void shouldGetNonSendedReminders(
        List<QuestionWithReminder> questionInfos, 
        Map<Long, List<Integer>> expectedResult) {

        questionInfos.forEach(this::saveQuestionWithReminder);

        Map<Long, List<Integer>> result = reminderDao.getNonSendedReminders();
        assertThat(result)
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> dataForGetNonSendedReminders() {
        return Stream.of(
            Arguments.of(List.of(), Map.of()),
            
            Arguments.of(
                List.of(
                    new QuestionWithReminder(1, 1L, true),
                    new QuestionWithReminder(2, 2L, true)
                ),
                Map.of()
            ),

            Arguments.of(
                List.of(
                    new QuestionWithReminder(1, 1L, false),
                    new QuestionWithReminder(2, 2L, false)
                ),
                Map.of(
                    1L, List.of(1),
                    2L, List.of(2)
                )
            )
        );
    }
 
    private record QuestionWithReminder (
        int id,
        long userId,
        boolean isSent
    ) {}

    private void saveQuestionWithReminder(QuestionWithReminder questionInfo) {
        jdbc.update(
            """
                INSERT INTO predictions.questions (id, text, deadline, created_at, author_id) 
                VALUES (:id, 'text', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :userId)
            """,
            Map.of(
                "id", questionInfo.id(),
                "userId", questionInfo.userId()
            )
        );

        jdbc.update(
            """
                INSERT INTO predictions.reminders (question_id, sent)
                VALUES (:id, :isSent)
            """,
            Map.of(
                "id", questionInfo.id(),
                "isSent", questionInfo.isSent()
            )
        );   
    }

}
