package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
class ReminderDaoImpl implements ReminderDao {
    private final NamedParameterJdbcOperations jdbc;

    ReminderDaoImpl(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void updateReminders() {
        jdbc.update(
            """
                INSERT INTO predictions.reminders
                SELECT id FROM predictions.questions
                LEFT JOIN predictions.reminders ON questions.id = reminders.question_id
                WHERE deadline < CURRENT_TIMESTAMP
                AND result IS NULL
                AND reminders.question_id IS NULL
            """, 
            Map.of());
    }

    @Override
    public Map<Long, List<Integer>> getNonSendedReminders() {
        List<UserQuestionPair> pairList = jdbc.query(
                """
                    SELECT author_id, question_id FROM predictions.questions
                    JOIN predictions.reminders ON questions.id = reminders.question_id
                    WHERE NOT reminders.sent
                    ORDER BY author_id, questions.deadline
                """,
                Map.of(),
                (rs, rowNum) -> new UserQuestionPair(
                    rs.getLong("author_id"), 
                    rs.getInt("question_id")
                )
        );

        Long currentUser = null;
        ArrayList<Integer> questionIds = new ArrayList<>();
        Map<Long, List<Integer>> result = new HashMap<>();
        for(UserQuestionPair pair : pairList) {
            if (!Objects.equals(currentUser, pair.userId())) {
                if (currentUser != null)
                    result.put(currentUser, questionIds);

                currentUser = pair.userId();
                questionIds = new ArrayList<>();                
            }

            questionIds.add(pair.questionId());
        }

        if (currentUser != null)
            result.put(currentUser, questionIds);
        
        return result;
    }

    private record UserQuestionPair(long userId, int questionId) {}

    @Override
    public void markReminderAsSended(int questionId) {
        jdbc.update(
            """
                UPDATE predictions.reminders
                SET sent = TRUE
                WHERE question_id = :questionId
            """,
            Map.of("questionId", questionId)
        );
    }

    @Override
    public void delete(int questionId) {
        jdbc.update(
            """
                DELETE FROM predictions.reminders
                WHERE question_id = :questionId
            """,
            Map.of("questionId", questionId)
        );
    }
}
