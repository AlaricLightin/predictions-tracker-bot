package io.github.alariclightin.predictionstrackerbot.commands.export;

import java.util.List;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDataForExport;

interface FileDataService {

    byte[] getFileData(List<PredictionDataForExport> dataList);
    
}
