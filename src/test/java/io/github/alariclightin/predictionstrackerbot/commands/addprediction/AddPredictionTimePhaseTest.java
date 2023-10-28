package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserTimezoneService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class AddPredictionTimePhaseTest {
    private AddPredictionTimePhase addPredictionTimePhase;
    private UserTimezoneService timezoneService;

    private static final Instant CURRENT_INSTANT = Instant.parse("2019-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(CURRENT_INSTANT, ZoneId.of("UTC"));
        timezoneService = mock(UserTimezoneService.class);
        addPredictionTimePhase = new AddPredictionTimePhase(clock, timezoneService);
    }
    
    @ParameterizedTest
    @MethodSource("dataForShouldHandleCorrectUserInput")
    void shouldHandleCorrectUserInput(
        String userInput,
        LocalDate dateInData,
        ZoneId userZoneId,
        Instant expectedInstantInData
    ) throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage(userInput);
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.TIME_PHASE, 
            new PredictionData()
                .addDate(dateInData)
        );
        when(timezoneService.getTimezone(TestUtils.CHAT_ID)).thenReturn(userZoneId);

        var result = addPredictionTimePhase.handle(message, state);

        assertThat(result.botMessage())
            .hasFieldOrPropertyWithValue("messageId", "bot.responses.ask-probability");
        
        assertThat(result.newState())
            .hasFieldOrPropertyWithValue("phase", AddPredictionConsts.PROBABILITY_PHASE)
            .extracting("data")
            .hasFieldOrPropertyWithValue("instant", expectedInstantInData);
    }

    @Test
    void shouldHandleIncorrectInput() {
        UserMessage message = TestUtils.createMessage("incorrect input");
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.TIME_PHASE, 
            new PredictionData()
                .addDate(LocalDate.parse("2019-01-01"))
        );

        var exception = assertThrows(
            UnexpectedUserMessageException.class, 
            () -> addPredictionTimePhase.handle(message, state)
        );

        assertThat(exception.getMessageId())
            .isEqualTo("bot.responses.error.wrong-time-format");
    }

    @Test
    void shouldHandlePastDateTime() {
        UserMessage message = TestUtils.createMessage("00:00");
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.TIME_PHASE, 
            new PredictionData()
                .addDate(LocalDate.parse("2019-01-01"))
        );
        when(timezoneService.getTimezone(TestUtils.CHAT_ID)).thenReturn(ZoneId.of("Israel"));

        var exception = assertThrows(
            UnexpectedUserMessageException.class, 
            () -> addPredictionTimePhase.handle(message, state)
        );

        assertThat(exception.getMessageId())
            .isEqualTo("bot.responses.error.past-date-time");
    }

    private static Stream<Arguments> dataForShouldHandleCorrectUserInput() {
        return Stream.of(
            Arguments.of(
                "00:00", 
                LocalDate.parse("2019-01-01"), 
                ZoneId.of("UTC"), 
                Instant.parse("2019-01-01T00:00:00Z")
            ),
            
            Arguments.of(
                "23:59", 
                LocalDate.parse("2019-01-01"), 
                ZoneId.of("UTC"), 
                Instant.parse("2019-01-01T23:59:00Z")
            ),
            
            Arguments.of(
                "00:00", 
                LocalDate.parse("2019-01-01"), 
                ZoneId.of("America/New_York"), 
                Instant.parse("2019-01-01T05:00:00Z")
            ),

            Arguments.of(
                "23:59", 
                LocalDate.parse("2019-01-01"), 
                ZoneId.of("Israel"), 
                Instant.parse("2019-01-01T21:59:00Z")
            )
        );
    }
}
