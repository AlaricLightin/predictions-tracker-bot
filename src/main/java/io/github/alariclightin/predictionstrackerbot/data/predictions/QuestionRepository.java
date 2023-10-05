package io.github.alariclightin.predictionstrackerbot.data.predictions;

import org.springframework.data.repository.CrudRepository;

interface QuestionRepository extends CrudRepository<Question, Integer> {
    
}
