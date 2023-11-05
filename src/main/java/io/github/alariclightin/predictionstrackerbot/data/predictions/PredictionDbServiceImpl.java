package io.github.alariclightin.predictionstrackerbot.data.predictions;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PredictionDbServiceImpl implements 
    PredictionDbService, 
    PredictionsResultDbService,
    PredictionsExportDbService {

    private final PredictionRepository predictionRepository;
    private final QuestionRepository questionRepository;
    private final ReminderDao reminderDao;

    public PredictionDbServiceImpl(
        PredictionRepository predictionRepository, 
        QuestionRepository questionRepository,
        ReminderDao reminderDao) {

        this.predictionRepository = predictionRepository;
        this.questionRepository = questionRepository;
        this.reminderDao = reminderDao;
    }

    @Override
    @Transactional
    public void addPrediction(Question question, Prediction prediction) {
        if (question.id() == 0) {
            var savedQuestion = questionRepository.save(question);
            prediction = prediction.cloneWithQuestionId(savedQuestion.id());
        }
        
        predictionRepository.save(prediction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getWaitingQuestionsIds(long userId) {
        return questionRepository.getWaitingQuestionsIds(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Question getQuestion(int questionId) {
        return questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Question with id = %d not found", questionId)));
    }

    @Override
    @Transactional
    public void setResult(int id, boolean result) {
        questionRepository.setResult(id, result);
        reminderDao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionDataForExport> getData(long userId) {
        return questionRepository.getPredictionsData(userId);
    }
    
}
