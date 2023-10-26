package io.github.alariclightin.predictionstrackerbot.data.predictions;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuestionRepositoryTest extends TestWithContainer {
    @Autowired
    private QuestionRepository questionRepository;

    private static final Long USER_ID = 123L;

    @Test
    @Sql(scripts = { "classpath:sql/waiting-question-ids.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldGetWaitingIds() {
        List<Integer> result = questionRepository.getWaitingQuestionsIds(USER_ID);
        assertThat(result)
            .containsExactly(10, 20);
    }
}
