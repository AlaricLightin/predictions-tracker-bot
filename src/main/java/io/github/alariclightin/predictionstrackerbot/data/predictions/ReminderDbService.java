package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;
import java.util.Map;

public interface ReminderDbService {
    void updateReminders();

    Map<Long, List<Integer>> getNonSendedReminders();

    void markReminderAsSent(int questionId);
}
