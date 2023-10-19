package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.List;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandlingResult;

public interface SetResultsMessageCreator {

    MessageHandlingResult createMessage(List<Integer> questionIds);
    
}
