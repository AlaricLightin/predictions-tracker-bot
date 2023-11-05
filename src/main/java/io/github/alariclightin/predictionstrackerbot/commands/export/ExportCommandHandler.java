package io.github.alariclightin.predictionstrackerbot.commands.export;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDataForExport;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsExportDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotFile;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class ExportCommandHandler implements MessageHandler {
    private final PredictionsExportDbService predictionsExportDbService;
    private final FileDataService fileDataService;

    ExportCommandHandler(PredictionsExportDbService predictionsExportDbService, FileDataService fileDataService) {
        this.predictionsExportDbService = predictionsExportDbService;
        this.fileDataService = fileDataService;
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        var userId = message.getUser().getId();
        List<PredictionDataForExport> dataList = predictionsExportDbService.getData(userId);
        if (!dataList.isEmpty()) {
            byte[] fileData = fileDataService.getFileData(dataList);
            return new ActionResult(
                new BotFile("export.csv", fileData)
            );
        }
        else {
            return new ActionResult(
                new BotTextMessage("bot.responses.no-predictions"));
        }
    }

    @Override
    public String getCommandName() {
        return "export";
    }
    
}
