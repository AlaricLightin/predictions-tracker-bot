package io.github.alariclightin.predictionstrackerbot.data.predictions;

import org.springframework.stereotype.Service;

@Service
class PredictionDbServiceImpl implements PredictionDbService {
    private final PredictionRepository predictionRepository;
    private final QuestionRepository questionRepository;

    public PredictionDbServiceImpl(
        PredictionRepository predictionRepository, 
        QuestionRepository questionRepository) {

        this.predictionRepository = predictionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void addPrediction(Question question, Prediction prediction) {
        if (question.id() == 0) {
            var savedQuestion = questionRepository.save(question);
            prediction = prediction.cloneWithQuestionId(savedQuestion.id());
        }
        
        predictionRepository.save(prediction);
    }
    
}
