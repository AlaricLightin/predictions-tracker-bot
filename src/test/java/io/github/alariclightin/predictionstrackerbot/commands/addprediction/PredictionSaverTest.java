package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

public class PredictionSaverTest {
    private PredictionSaver predictionSaver;
    private PredictionDbService predictionDbService;

    @BeforeEach
    void setUp() {
        predictionDbService = mock(PredictionDbService.class);
        predictionSaver = new PredictionSaver(predictionDbService);
    }

    @Test
    void shouldSavePrediction() {
        final String predictionText = "Prediction text";
        final LocalDate deadlineDate = LocalDate.of(2024, 1, 1);
        final LocalTime deadlineTime = LocalTime.of(12, 0);
        final int probability = 60;
        PredictionData data = new PredictionData()
                .addText(predictionText)
                .addDate(deadlineDate)
                .addTime(deadlineTime)
                .addProbability(probability);
        
        Message message = TestUtils.createTestMessage(false, "60");

        predictionSaver.apply(message, data);
        
        ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
        ArgumentCaptor<Prediction> predictionCaptor = ArgumentCaptor.forClass(Prediction.class);
        
        verify(predictionDbService).addPrediction(questionCaptor.capture(), predictionCaptor.capture());
        assertThat(questionCaptor.getValue())
            .hasFieldOrPropertyWithValue("text", predictionText)
            .hasFieldOrPropertyWithValue("deadline", deadlineDate.atTime(deadlineTime).toInstant(ZoneOffset.UTC))
            .hasFieldOrPropertyWithValue("authorId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("result", null);
        
        assertThat(predictionCaptor.getValue())
            .hasFieldOrPropertyWithValue("userId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("probability", probability);
    }
}
