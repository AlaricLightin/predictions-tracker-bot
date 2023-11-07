package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "predictions", schema = "predictions")
public record Prediction(
    @Id
    int id,

    @Column("question_id")
    int questionId,

    @Column("user_id")
    long userId,

    @Column("created_at")
    Instant createdAt,

    @Column("confidence")
    int confidence
) {
    public Prediction(Question question, long userId, Instant createdAt, int confidence) {
        this(0, question.id(), userId, createdAt, confidence);
    }

    public Prediction cloneWithQuestionId(int questionId) {
        return new Prediction(this.id, questionId, this.userId, this.createdAt, this.confidence);
    }
}
