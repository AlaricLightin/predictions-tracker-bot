package io.github.alariclightin.predictionstrackerbot.schedulers;

import java.util.List;
import java.util.Optional;

interface ReminderSender {

    /**
     * Send one reminder to user about questions waiting for result
     * @param userId id of user to send reminder
     * @param questionIds ids of questions waiting for result
     * @return id of question in sended reminder
     */
    Optional<Integer> sendOneReminderToUser(Long userId, List<Integer> questionIds);

}
