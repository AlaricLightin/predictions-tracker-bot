package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;
import java.util.Map;

interface ReminderDao {
    void updateReminders();

    Map<Long, List<Integer>> getNonSendedReminders();

    void markReminderAsSended(int questionId);

    void delete(int questionId);
}
