package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.ArrayList;
import java.util.List;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;

abstract class AbstractSetResultsHandler implements MessageHandler {
    private final PredictionsResultDbService predictionsResultDbService;

    AbstractSetResultsHandler(PredictionsResultDbService predictionsResultDbService) {
        this.predictionsResultDbService = predictionsResultDbService;
    }

    protected static final String COMMAND = "setresults";
    protected static final String SET_RESULT_PHASE = "set-result";

    @Override
    public String getCommandName() {
        return COMMAND;
    }

    protected QuestionsData getHandlingResult(ArrayList<Integer> arrayList) {
        while(!arrayList.isEmpty()) {
            int questionId = arrayList.remove(0);
            Question question = predictionsResultDbService.getQuestion(questionId);

            if (question.result() == null) {
                return new QuestionsData(arrayList, question);
            }
        }
        
        return new QuestionsData(new ArrayList<>(), null);
    }

    protected ArrayList<Integer> getWaitingQuestionsIdsFromDb(long userId) {
        List<Integer> waitingQuestionsIds = predictionsResultDbService.getWaitingQuestionsIds(userId);
        return new ArrayList<Integer>(waitingQuestionsIds);
    }

    protected void addResultToDb(Question question, boolean result) {
        predictionsResultDbService.setResult(question.id(), result);
    }

    protected BotMessage getPromptForResult(Question question) {
        return new BotTextMessage("bot.responses.setresults.set-result", 
            question.text(), question.deadline());
    }

    protected BotMessage getNoPredictionsMessage() {
        return new BotTextMessage("bot.responses.setresults.no-questions-to-set-results");
    }

}
