package io.github.alariclightin.predictionstrackerbot.commands.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionDataForExport;

@Service
class CsvService implements FileDataService {
    private final CsvMapper csvMapper;

    CsvService(CsvMapper csvMapper) {
        this.csvMapper = csvMapper;
    }

    @Override
    public byte[] getFileData(List<PredictionDataForExport> dataList) {
        CsvSchema csvSchema = new CsvMapper().schemaFor(PredictionDataForExport.class)
            .withHeader()
            .withColumnSeparator(';');

        try (var outputStream = new ByteArrayOutputStream()) {
            csvMapper.writerFor(PredictionDataForExport.class)
                .with(csvSchema)
                .writeValues(outputStream)
                .writeAll(dataList);
            return outputStream.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
