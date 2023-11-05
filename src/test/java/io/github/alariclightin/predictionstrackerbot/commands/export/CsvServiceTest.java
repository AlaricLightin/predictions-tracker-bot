package io.github.alariclightin.predictionstrackerbot.commands.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDataForExport;

class CsvServiceTest {
    private CsvService csvService;

    @BeforeEach
    void setUp() {
        var csvMapper = new CsvMapper();
        csvMapper.findAndRegisterModules();
        csvService = new CsvService(csvMapper);
    }

    @Test
    void shouldGetCsvData() {
        var dataList = List.of(
            new PredictionDataForExport(
                "Prediction 1",
                OffsetDateTime.parse("2021-01-01T00:00:00Z"),
                OffsetDateTime.parse("2021-01-02T00:00:00Z"),
                90,
                true
            ),

            new PredictionDataForExport(
                "Prediction 2",
                OffsetDateTime.parse("2021-01-03T00:00:00Z"),
                OffsetDateTime.parse("2021-01-04T00:00:00Z"),
                10,
                null
            )
        );

        var result = csvService.getFileData(dataList);
        var resultString = new String(result, StandardCharsets.UTF_8);

        assertThat(resultString)
            .contains("Prediction 1", "Prediction 2", "2021-01-01T00:00:00Z");

        assertThat(resultString.split("\n").length)
            .isEqualTo(3);
    }
}
