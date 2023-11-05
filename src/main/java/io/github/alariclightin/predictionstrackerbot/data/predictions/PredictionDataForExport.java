package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"text", "createdAt", "deadline", "probability", "result"})
public record PredictionDataForExport(
    String text,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    OffsetDateTime createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    OffsetDateTime deadline,

    int probability,

    Boolean result
) {
    
}
