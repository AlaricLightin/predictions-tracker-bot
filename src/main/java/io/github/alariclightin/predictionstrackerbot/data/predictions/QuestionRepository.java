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
    
}
