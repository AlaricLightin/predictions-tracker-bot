package io.github.alariclightin.predictionstrackerbot.schedulers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.github.alariclightin.predictionstrackerbot.botservice.BotService;
import io.github.alariclightin.predictionstrackerbot.botservice.SendMessageService;
import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.QuestionsData;
import io.github.alariclightin.predictionstrackerbot.commands.setresult.SetResultsMessageCreator;
import io.github.alariclightin.predictionstrackerbot.states.StateHolderService;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Service
class ReminderSenderImpl implements ReminderSender {
    private final SetResultsMessageCreator setResultsMessageCreator;
    private final StateHolderService stateHolderService;
    private final SendMessageService sendMessageService;
    private final BotService botService;

    ReminderSenderImpl(
        SetResultsMessageCreator setResultsMessageCreator,
        StateHolderService stateHolderService,
        SendMessageService sendMessageService,
        BotService botService) {
        
        this.setResultsMessageCreator = setResultsMessageCreator;
        this.stateHolderService = stateHolderService;
        this.sendMessageService = sendMessageService;
        this.botService = botService;
    }

    @Override
    public Optional<Integer> sendOneReminderToUser(Long userId, List<Integer> questionIds) {
        WaitedResponseState state = stateHolderService.getState(userId);       
        if (state != null) {// User is doing something 
            return Optional.empty();
        }
        
        ActionResult actionResult = setResultsMessageCreator.createMessage(questionIds);
        if (actionResult == null) // No predictions to remind about
            return Optional.empty();

        if (actionResult.newState() == null || 
            !(actionResult.newState().data() instanceof QuestionsData questionsData))
            throw new IllegalStateException("Wrong state returned from SetResultsMessageCreator");
        
        stateHolderService.saveState(userId, actionResult.newState());
        SendMessage sendMessage = sendMessageService.create(userId, actionResult.botMessage());
        botService.sendMessage(sendMessage);
        return Optional.of(questionsData.question().id());
    }
    
}
