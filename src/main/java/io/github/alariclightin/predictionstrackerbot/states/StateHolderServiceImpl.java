package io.github.alariclightin.predictionstrackerbot.states;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
// TODO later replace to storage data in DB
class StateHolderServiceImpl implements StateHolderService {
    private final Map<Long, WaitedResponseState> states = new ConcurrentHashMap<>();

    @Override
    public void saveState(long userId, WaitedResponseState state) {
        if (state != null)
            states.put(userId, state);
        else
            deleteState(userId);
    }

    @Override
    public WaitedResponseState getState(long userId) {
        return states.get(userId);
    }

    @Override
    public void deleteState(long userId) {
        states.remove(userId);
    }
    
}
