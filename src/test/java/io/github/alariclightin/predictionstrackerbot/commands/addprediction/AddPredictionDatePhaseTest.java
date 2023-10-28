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

class AddPredictionDatePhaseTest {
    private AddPredictionDatePhase addPredictionDatePhase;
    private UserTimezoneService timezoneService;

    private static final Instant CURRENT_INSTANT = Instant.parse("2019-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(CURRENT_INSTANT, ZoneId.of("UTC"));
        timezoneService = mock(UserTimezoneService.class);
        addPredictionDatePhase = new AddPredictionDatePhase(clock, timezoneService);
    }

    @ParameterizedTest
    @MethodSource("dataForShouldHandleCorrectUserInput")
    void shouldHandleCorrectUserInput(
        String userInput,
        ZoneId userZoneId,
        String expectedResponseId,
        String expectedNextPhase,
        LocalDate expectedDateInData,
        Instant expectedInstantInData
    ) throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage(userInput);
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.DATE_PHASE, new PredictionData()
        );
        when(timezoneService.getTimezone(TestUtils.CHAT_ID)).thenReturn(userZoneId);
        
        var result = addPredictionDatePhase.handle(message, state);

        assertThat(result.botMessage())
            .hasFieldOrPropertyWithValue("messageId", expectedResponseId);

        assertThat(result.newState())
            .hasFieldOrPropertyWithValue("phase", expectedNextPhase);

        assertThat(result.newState().data())
            .isInstanceOf(PredictionData.class)
            .hasFieldOrPropertyWithValue("date", expectedDateInData)
            .hasFieldOrPropertyWithValue("instant", expectedInstantInData);
    }

    @Test
    void shouldShowErrorIfInvalidUserInput() {
        UserMessage message = TestUtils.createMessage("invalid input");
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.DATE_PHASE, new PredictionData()
        );

        var exception = assertThrows(UnexpectedUserMessageException.class, 
            () -> addPredictionDatePhase.handle(message, state));

        assertThat(exception.getMessageId())
            .isEqualTo("bot.responses.error.wrong-date-time-format");
    }

    @Test
    void shouldShowErrorIfInputedDateBeforeCurrentDate() {
        UserMessage message = TestUtils.createMessage("2018-01-01");
        WaitedResponseState state = new WaitedResponseState(
            AddPredictionConsts.COMMAND_NAME, AddPredictionConsts.DATE_PHASE, new PredictionData()
        );
        when(timezoneService.getTimezone(TestUtils.CHAT_ID)).thenReturn(ZoneId.of("UTC"));

        var exception = assertThrows(UnexpectedUserMessageException.class, 
            () -> addPredictionDatePhase.handle(message, state));

        assertThat(exception.getMessageId())
            .isEqualTo("bot.responses.error.past-date-time");
    }

    private static Stream<Arguments> dataForShouldHandleCorrectUserInput() {
        return Stream.of(
            Arguments.of(
                "2020-01-01 02:00",
                ZoneId.of("Europe/Moscow"),
                "bot.responses.ask-probability",
                AddPredictionConsts.PROBABILITY_PHASE,
                null,
                Instant.parse("2019-12-31T23:00:00Z")
            ),

            Arguments.of(
                "2020-01-01",
                ZoneId.of("Europe/Moscow"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2020-01-01"),
                null
            ),

            Arguments.of(
                "2019-01-01", // same date as current
                ZoneId.of("UTC"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2019-01-01"),
                null
            ),

            Arguments.of(
                "ONE_MINUTE",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-probability",
                AddPredictionConsts.PROBABILITY_PHASE,
                null,
                CURRENT_INSTANT.plusSeconds(60)
            ),

            Arguments.of(
                "ONE_HOUR",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-probability",
                AddPredictionConsts.PROBABILITY_PHASE,
                null,
                CURRENT_INSTANT.plusSeconds(3600)
            ),

            Arguments.of(
                "TODAY",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2019-01-01"),
                null
            ),

            Arguments.of(
                "TODAY",
                ZoneId.of("America/New_York"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2018-12-31"),
                null
            ),

            Arguments.of(
                "TOMORROW",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2019-01-02"),
                null
            ),

            Arguments.of(
                "NEXT_MONTH",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2019-02-01"),
                null
            ),

            Arguments.of(
                "NEXT_YEAR",
                ZoneId.of("Europe/Paris"),
                "bot.responses.ask-deadline-time",
                AddPredictionConsts.TIME_PHASE,
                LocalDate.parse("2020-01-01"),
                null
            )

        );
    }

}
