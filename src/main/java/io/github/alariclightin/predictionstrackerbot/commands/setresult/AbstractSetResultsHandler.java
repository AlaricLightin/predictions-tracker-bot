package io.github.alariclightin.predictionstrackerbot.commands.setresult;

import java.util.ArrayList;
import java.util.List;

import io.github.alariclightin.predictionstrackerbot.commands.MessageHandler;
import io.github.alariclightin.predictionstrackerbot.data.predictions.PredictionsResultDbService;
import io.github.alariclightin.predictionstrackerbot.data.predictions.Question;
import io.github.alariclightin.predictionstrackerbot.data.predictions.ReminderDbService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotKeyboard;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessageList;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotTextMessage;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;

abstract class AbstractSetResultsHandler implements MessageHandler {
    private final PredictionsResultDbService predictionsResultDbService;
    private final ReminderDbService reminderDbService;

    AbstractSetResultsHandler(
        PredictionsResultDbService predictionsResultDbService,
        ReminderDbService reminderDbService) {
        
        this.predictionsResultDbService = predictionsResultDbService;
        this.reminderDbService = reminderDbService;
    }

    protected static final String COMMAND = "setresults";
    protected static final String SET_RESULT_PHASE = "set-result";

    private static final BotKeyboard KEYBOARD = BotKeyboard.createOneRowKeyboard(
        new InlineButton("bot.buttons.yes", "setresults.yes"),
        new InlineButton("bot.buttons.no", "setresults.no"),
        new InlineButton("bot.buttons.skip", "setresults.skip"),
        new InlineButton("bot.buttons.skip-all", "setresults.skip-all")
    );

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
        return new BotMessageList(
            new BotTextMessage("bot.responses.setresults.set-result", 
                question.text(), question.deadline()),
            KEYBOARD
        );
    }

    protected BotMessage getNoPredictionsMessage() {
        return new BotTextMessage("bot.responses.setresults.no-questions-to-set-results");
    }

    // TODO maybe it's better to do this after sending the real message
    protected void markReminderAsSent(int questionId) {
        reminderDbService.markReminderAsSent(questionId);
    }

}
