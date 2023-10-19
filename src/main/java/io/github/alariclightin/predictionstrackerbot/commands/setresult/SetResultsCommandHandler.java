package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class SetResultsCommandHandler extends AbstractSetResultsHandler implements SetResultsMessageCreator {
    
    SetResultsCommandHandler(PredictionsResultDbService predictionsResultDbService) {
        super(predictionsResultDbService);
    }

    @Override
    public ActionResult handle(Message message, WaitedResponseState state) throws UnexpectedUserMessageException {
        long userId = message.getFrom().getId();
        ArrayList<Integer> waitingPredictionsIds = getWaitingQuestionsIdsFromDb(userId);
        QuestionsData questionsData = getHandlingResult(waitingPredictionsIds);
        if (questionsData.question() != null) {
            return new ActionResult(
                getPromptForResult(questionsData.question()), 
                new WaitedResponseState(COMMAND, SET_RESULT_PHASE, questionsData)
            );
        }
        else {
            return new ActionResult(
                getNoPredictionsMessage(), 
                null
            );
        }
    }

    @Override
    public String getPhaseName() {
        return MessageHandler.START_PHASE;
    }

    @Override
    public ActionResult createMessage(List<Integer> questionIds) {
        QuestionsData questionsData = getHandlingResult(new ArrayList<>(questionIds));
        if (questionsData.question() != null) {
            return new ActionResult(
                getPromptForResult(questionsData.question()), 
                new WaitedResponseState(COMMAND, SET_RESULT_PHASE, questionsData)
            );
        }
        else    
            return null;
    }
    
}