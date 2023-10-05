package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "questions", schema = "predictions")
public record Question(
    @Id 
    int id,

    @Column("text")
    String text,

    @Column("deadline")
    Instant deadline,

    @Column("author_id")
    long authorId,

    @Column("result")
    Boolean result
) {
    public Question(String text, Instant deadline, long authorId) {
        this(0, text, deadline, authorId, null);
    }
}
