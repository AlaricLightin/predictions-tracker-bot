package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class PredictionSaverTest {
    private PredictionSaver predictionSaver;
    private PredictionDbService predictionDbService;

    @BeforeEach
    void setUp() {
        predictionDbService = mock(PredictionDbService.class);
        predictionSaver = new PredictionSaver(predictionDbService);
    }

    @Test
    void shouldSavePrediction() {
        var data = new PredictionData()
            .addText("Prediction text")
            .addDate(LocalDate.of(2022, 1, 1))
            .addInstant(LocalDateTime.of(2022, 1, 1, 12, 0).toInstant(ZoneOffset.UTC))
            .addProbability(60);

        UserMessage message = TestUtils.createMessage(Integer.toString(data.getProbability()));

        predictionSaver.apply(message, data);
        
        ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
        ArgumentCaptor<Prediction> predictionCaptor = ArgumentCaptor.forClass(Prediction.class);
        
        verify(predictionDbService).addPrediction(questionCaptor.capture(), predictionCaptor.capture());
        assertThat(questionCaptor.getValue())
            .hasFieldOrPropertyWithValue("text", data.getText())
            .hasFieldOrPropertyWithValue("deadline", data.getInstant())
            .hasFieldOrPropertyWithValue("authorId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("result", null);
        
        assertThat(predictionCaptor.getValue())
            .hasFieldOrPropertyWithValue("userId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("probability", data.getProbability());
    }

}
