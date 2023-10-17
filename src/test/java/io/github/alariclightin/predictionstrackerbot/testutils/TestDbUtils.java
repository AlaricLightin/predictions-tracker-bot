package io.github.alariclightin.predictionstrackerbot.testutils;

import org.springframework.jdbc.core.JdbcTemplate;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;

public class TestDbUtils {
    public static Question getQuestionById(JdbcTemplate jdbcTemplate, int id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM predictions.questions WHERE id = ?",
            (rs, rowNum) -> new Question(
                rs.getInt("id"),
                rs.getString("text"),
                rs.getTimestamp("deadline").toInstant(),
                rs.getLong("author_id"),
                rs.getString("result") == null ? null : rs.getBoolean("result")
            ),
            id
        );
    }
}
