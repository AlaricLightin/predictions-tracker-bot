package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;

public interface PredictionsResultDbService {

    List<Integer> getWaitingQuestionsIds(long userId);

    Question getQuestion(int questionId);

    void setResult(int id, boolean result);
    
}
