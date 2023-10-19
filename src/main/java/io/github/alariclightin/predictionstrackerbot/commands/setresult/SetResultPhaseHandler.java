package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.commands.ActionResult;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedUserMessageException;
import io.github.alariclightin.predictionstrackerbot.messages.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

@Component
class SetResultPhaseHandler extends AbstractSetResultsHandler {

    SetResultPhaseHandler(PredictionsResultDbService predictionsResultDbService) {
        super(predictionsResultDbService);
    }

    @Override
    public ActionResult handle(Message message, WaitedResponseState state) 
        throws UnexpectedUserMessageException {
            
        if (!(state.data() instanceof QuestionsData questionsData)) 
            throw new IllegalStateException("Unexpected state data type: " + state.data().getClass().getName());

        Question question = questionsData.question();
        if (question == null)
            throw new IllegalStateException("Unexpected state data: question is null");

        // TODO Add internationalization
        ResultUserCommand command;
        try {
            command = ResultUserCommand.valueOf(message.getText().toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new UnexpectedUserMessageException("bot.responses.error.wrong-result-command");
        }
        
        BotTextMessage buttonResultMessage = null;
        switch (command) {
            case YES, NO -> {
                addResultToDb(question, command == ResultUserCommand.YES);
                buttonResultMessage = new BotTextMessage("bot.responses.result-saved");
            }
            
            case SKIP -> {
                buttonResultMessage = new BotTextMessage("bot.responses.result-skipped");
            }

            case SKIP_ALL -> {
                return new ActionResult(
                    new BotTextMessage("bot.responses.result-skipped-all"), 
                    null);
            }

            default -> throw new UnexpectedUserMessageException("bot.responses.error.wrong-result-command");
        }

        questionsData = getHandlingResult(questionsData.waitingQuestionsIds());
        if (questionsData.question() != null) {
            return new ActionResult(
                new BotMessageList(
                    List.of(buttonResultMessage, getPromptForResult(questionsData.question()))
                ), 
                new WaitedResponseState(COMMAND, SET_RESULT_PHASE, questionsData)
            );
        }
        else {
            return new ActionResult(
                buttonResultMessage, 
                null
            );
        }
    }

    @Override
    public String getPhaseName() {
        return SET_RESULT_PHASE;
    }
    
}
