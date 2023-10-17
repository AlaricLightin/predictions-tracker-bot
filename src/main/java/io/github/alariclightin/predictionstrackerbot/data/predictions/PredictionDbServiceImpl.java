package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
class PredictionDbServiceImpl implements PredictionDbService, PredictionsResultDbService {
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

    @Override
    public List<Integer> getWaitingQuestionsIds(long userId) {
        return questionRepository.getWaitingQuestionsIds(userId);
    }

    @Override
    public Question getQuestion(int questionId) {
        return questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Question with id = %d not found", questionId)));
    }

    @Override
    public void setResult(int id, boolean result) {
        questionRepository.setResult(id, result);
    }
    
}
