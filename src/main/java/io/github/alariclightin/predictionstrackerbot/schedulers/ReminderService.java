package io.github.alariclightin.predictionstrackerbot.schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.alariclightin.predictionstrackerbot.data.predictions.ReminderDbService;

@Service
class ReminderService {
    private final ReminderDbService reminderDbService;
    private final ReminderSender reminderSender;

    ReminderService(
        ReminderDbService reminderDbService,
        ReminderSender reminderSender) {
        
        this.reminderDbService = reminderDbService;
        this.reminderSender = reminderSender;
    }
    
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void updateReminders() {
        reminderDbService.updateReminders();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void sendReminders() {
        Map<Long, List<Integer>> questionIdsMap = reminderDbService.getNonSendedReminders();
        questionIdsMap.forEach((userId, questionIds) -> {
            reminderSender.sendOneReminderToUser(userId, questionIds);
        });
    }
}
