package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;

public interface PredictionsExportDbService {
    List<PredictionDataForExport> getData(long userId);
}
