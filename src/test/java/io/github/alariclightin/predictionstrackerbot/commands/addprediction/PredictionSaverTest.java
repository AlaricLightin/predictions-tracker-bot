package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Prediction;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class PredictionSaverTest {
    private PredictionSaver predictionSaver;
    private PredictionDbService predictionDbService;
    private UserTimezoneService userTimezoneService;

    @BeforeEach
    void setUp() {
        predictionDbService = mock(PredictionDbService.class);
        userTimezoneService = mock(UserTimezoneService.class);
        predictionSaver = new PredictionSaver(predictionDbService, userTimezoneService);
    }

    @ParameterizedTest
    @MethodSource("dataForShouldSavePrediction")
    void shouldSavePrediction(PredictionData data, String zoneIdString, Instant expectedDeadline) {      
        UserMessage message = TestUtils.createMessage(Integer.toString(data.getProbability()));
        when(userTimezoneService.getTimezone(TestUtils.CHAT_ID)).thenReturn(ZoneId.of(zoneIdString));

        predictionSaver.apply(message, data);
        
        ArgumentCaptor<Question> questionCaptor = ArgumentCaptor.forClass(Question.class);
        ArgumentCaptor<Prediction> predictionCaptor = ArgumentCaptor.forClass(Prediction.class);
        
        verify(predictionDbService).addPrediction(questionCaptor.capture(), predictionCaptor.capture());
        assertThat(questionCaptor.getValue())
            .hasFieldOrPropertyWithValue("text", data.getText())
            .hasFieldOrPropertyWithValue("deadline", expectedDeadline)
            .hasFieldOrPropertyWithValue("authorId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("result", null);
        
        assertThat(predictionCaptor.getValue())
            .hasFieldOrPropertyWithValue("userId", TestUtils.CHAT_ID)
            .hasFieldOrPropertyWithValue("probability", data.getProbability());
    }

    private static Stream<Arguments> dataForShouldSavePrediction() {
        return Stream.of(
            Arguments.of(
                new PredictionData()
                    .addText("Prediction text")
                    .addDate(LocalDate.of(2022, 1, 1))
                    .addTime(LocalTime.of(12, 0))
                    .addProbability(60),
                "UTC",
                LocalDate.of(2022, 1, 1).atTime(12, 0).toInstant(ZoneOffset.UTC)
            ),

            Arguments.of(
                new PredictionData()
                    .addText("Another prediction text")
                    .addDate(LocalDate.of(2022, 1, 1))
                    .addTime(LocalTime.of(12, 0))
                    .addProbability(60),
                "Europe/Moscow",
                LocalDate.of(2022, 1, 1).atTime(9, 0).toInstant(ZoneOffset.UTC)
            )
        );
    }
}
