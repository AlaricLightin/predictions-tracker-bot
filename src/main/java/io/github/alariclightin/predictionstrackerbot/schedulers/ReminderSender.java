package io.github.alariclightin.predictionstrackerbot.schedulers;

import java.util.List;

interface ReminderSender {

    /**
     * Send one reminder to user about questions waiting for result
     * @param userId id of user to send reminder
     * @param questionIds ids of questions waiting for result
     */
    void sendOneReminderToUser(long userId, List<Integer> questionIds);

}
