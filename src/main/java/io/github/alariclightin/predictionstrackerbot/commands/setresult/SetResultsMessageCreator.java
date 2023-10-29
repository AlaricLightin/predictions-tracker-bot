package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.List;

import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;

public interface SetResultsMessageCreator {

    ActionResult createMessage(List<Integer> questionIds);
    
}
