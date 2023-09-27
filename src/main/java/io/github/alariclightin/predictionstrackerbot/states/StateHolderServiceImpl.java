package io.github.alariclightin.predictionstrackerbot.states;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
// TODO later replace to storage data in DB
class StateHolderServiceImpl implements StateHolderService {
    private final Map<Long, WaitedResponseState> states = new HashMap<>();

    @Override
    public void saveState(long userId, WaitedResponseState state) {
        states.put(userId, state);
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
