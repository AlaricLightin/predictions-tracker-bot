package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.ReminderDbService;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.incoming.UserMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class SetResultsCommandHandler extends AbstractSetResultsHandler implements SetResultsMessageCreator {
    
    SetResultsCommandHandler(
        PredictionsResultDbService predictionsResultDbService,
        ReminderDbService reminderDbService) {
        
        super(predictionsResultDbService, reminderDbService);
    }

    @Override
    public ActionResult handle(UserMessage message, WaitedResponseState state) throws UnexpectedUserMessageException {
        long userId = message.getUser().getId();
        ArrayList<Integer> waitingPredictionsIds = getWaitingQuestionsIdsFromDb(userId);
        QuestionsData questionsData = getHandlingResult(waitingPredictionsIds);
        if (questionsData.question() != null) {
            markReminderAsSent(questionsData.question().id());
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
            markReminderAsSent(questionsData.question().id());
            return new ActionResult(
                getPromptForResult(questionsData.question()), 
                new WaitedResponseState(COMMAND, SET_RESULT_PHASE, questionsData)
            );
        }
        else    
            return null;
    }
    
}
