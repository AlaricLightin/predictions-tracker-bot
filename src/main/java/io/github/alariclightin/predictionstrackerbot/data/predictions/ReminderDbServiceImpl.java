package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ReminderDbServiceImpl implements ReminderDbService {
    private final ReminderDao reminderDao;

    public ReminderDbServiceImpl(ReminderDao reminderDao) {
        this.reminderDao = reminderDao;
    }

    @Override
    @Transactional
    public void updateReminders() {
        reminderDao.updateReminders();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Integer>> getNonSendedReminders() {
        return reminderDao.getNonSendedReminders();
    }

    @Override
    @Transactional
    public void markReminderAsSent(int questionId) {
        reminderDao.markReminderAsSended(questionId);
    }
    
}
