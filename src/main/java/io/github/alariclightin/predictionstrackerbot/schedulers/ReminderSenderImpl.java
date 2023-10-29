package io.github.alariclightin.predictionstrackerbot.schedulers;

import java.util.List;
import org.springframework.stereotype.Service;

import io.github.alariclightin.predictionstrackerbot.commands.setresult.QuestionsData;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.SetResultsMessageCreator;
import io.github.alariclightin.predictionstrackerbot.messagehandlers.ActionResult;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class ReminderSenderImpl implements ReminderSender {
    private final SetResultsMessageCreator setResultsMessageCreator;
    private final StateHolderService stateHolderService;
    private final ReminderGateway reminderGateway;

    ReminderSenderImpl(
        SetResultsMessageCreator setResultsMessageCreator,
        StateHolderService stateHolderService,
        ReminderGateway reminderGateway) {
        
        this.setResultsMessageCreator = setResultsMessageCreator;
        this.stateHolderService = stateHolderService;
        this.reminderGateway = reminderGateway;
    }

    @Override
    public void sendOneReminderToUser(long userId, List<Integer> questionIds) {
        WaitedResponseState state = stateHolderService.getState(userId);       
        if (state != null) {// User is doing something 
            return;
        }
        
        ActionResult actionResult = setResultsMessageCreator.createMessage(questionIds);
        if (actionResult == null) // No predictions to remind about
            return;

        if (actionResult.newState() == null || 
            !(actionResult.newState().data() instanceof QuestionsData questionsData))
            throw new IllegalStateException("Wrong state returned from SetResultsMessageCreator");
        
        stateHolderService.saveState(userId, actionResult.newState());
        reminderGateway.sendBotMessage(userId, actionResult.botMessage());
    }
    
}
