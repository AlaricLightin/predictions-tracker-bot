package io.github.alariclightin.predictionstrackerbot.data.predictions;

import org.springframework.data.repository.CrudRepository;

interface PredictionRepository extends CrudRepository<Prediction, Integer> {
    
}
