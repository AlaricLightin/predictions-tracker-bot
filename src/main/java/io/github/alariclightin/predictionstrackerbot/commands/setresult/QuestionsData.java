package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.ArrayList;

import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;

record QuestionsData(
    ArrayList<Integer> waitingQuestionsIds,
    Question question
) {
    
}
