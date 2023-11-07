package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

interface QuestionRepository extends CrudRepository<Question, Integer> {

    @Query("""
            SELECT id FROM predictions.questions 
            WHERE author_id = :userId 
            AND result IS NULL
            AND deadline < CURRENT_TIMESTAMP
            ORDER BY deadline
           """)
    List<Integer> getWaitingQuestionsIds(long userId);

    @Modifying
    @Query("UPDATE predictions.questions SET result = :result WHERE id = :id")
    void setResult(int id, boolean result);

    @Query("""
            SELECT 
                q.text AS text,
                q.deadline AS deadline,
                q.author_id AS author_id,
                q.created_at AS created_at,
                q.result AS result,
                p.confidence AS confidence
            FROM predictions.questions q
            JOIN predictions.predictions p ON q.id = p.question_id
            WHERE q.author_id = :userId
                AND p.user_id = :userId
            ORDER BY q.created_at
           """)
    List<PredictionDataForExport> getPredictionsData(long userId);
    
}
