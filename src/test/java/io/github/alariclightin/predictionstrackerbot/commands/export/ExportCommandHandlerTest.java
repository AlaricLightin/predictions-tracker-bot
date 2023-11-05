package io.github.alariclightin.predictionstrackerbot.commands.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDataForExport;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsExportDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotFile;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageAssert;
import io.github.alariclightin.predictionstrackerbot.testutils.TestUtils;

class ExportCommandHandlerTest {
    private ExportCommandHandler exportCommandHandler;
    private PredictionsExportDbService predictionsExportDbService;
    private FileDataService fileDataService;

    @BeforeEach
    void setUp() {
        predictionsExportDbService = mock(PredictionsExportDbService.class);
        fileDataService = mock(FileDataService.class);
        exportCommandHandler = new ExportCommandHandler(predictionsExportDbService, fileDataService);
    }

    @Test
    void shouldHandleExportCommandIfPredictionsExist() throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage("export");
        List<PredictionDataForExport> dataList = createList();
        String resultContent = "result";
        when(fileDataService.getFileData(dataList)).thenReturn(resultContent.getBytes());
        when(predictionsExportDbService.getData(TestUtils.CHAT_ID)).thenReturn(dataList);
        
        var result = exportCommandHandler.handle(message, null);

        assertThat(result.botMessage())
            .asInstanceOf(InstanceOfAssertFactories.type(BotFile.class))
            .hasFieldOrPropertyWithValue("filename", "export.csv")
            .hasFieldOrPropertyWithValue("content", resultContent.getBytes());

        assertThat(result.newState())
            .isNull();
    }

    @Test
    void shouldHandleExportCommandIfNoPredictionExists() throws UnexpectedUserMessageException {
        UserMessage message = TestUtils.createMessage("export");
        when(predictionsExportDbService.getData(TestUtils.CHAT_ID)).thenReturn(List.of());
        
        var result = exportCommandHandler.handle(message, null);

        BotMessageAssert.assertIsTextBotMessageWithId(result.botMessage(), "bot.responses.no-predictions");

        assertThat(result.newState())
            .isNull();
    }

    private List<PredictionDataForExport> createList() {
        return List.of(
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
    }
}
