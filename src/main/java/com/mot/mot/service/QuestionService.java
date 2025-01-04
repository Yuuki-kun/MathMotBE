package com.mot.mot.service;

import com.mot.mot.model.dto.QuestionDto;
import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.Question;
import com.mot.mot.repository.AnswerRepository;
import com.mot.mot.repository.QuestionRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService extends CrudServiceImpl<Question> implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        super(questionRepository);
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public Question create(Object object) {
        return null;
    }

    @Override
    public List<Question> getQuestions(int offset, int size, long examId) {
        return questionRepository.getQuestionsByOffsetAndSizeAndExamId(offset, size, examId);
    }

    @Override
    @Transactional
    public void update(Question question) throws BadRequestException {

        var q = getById(question.getId());

        if(q == null) {
            throw new BadRequestException("Question not found with ID: " + question.getId());
        }

        q.setPoint(question.getPoint());
        q.setTitle(question.getTitle());
        q.setType(question.getType());
        q.setLevel(question.getLevel());
        q.setPoint(question.getPoint());

        List<Answer> newAnswers = question.getAnswers();
        List<Answer> currentAnswers = q.getAnswers();

       List<Answer> updatedAnswers = newAnswers.stream().map(answerDto -> {
           if(answerDto.getId() == null) {
               return Answer.builder().content(answerDto.getContent()).correct(answerDto.getCorrect()).letter(answerDto.getLetter()).build();
           } else {
               return currentAnswers.stream().filter(answer -> answer.getId().equals(answerDto.getId())).findFirst().map(answer -> {
                   answer.setContent(answerDto.getContent());
                   answer.setCorrect(answerDto.getCorrect());
                   answer.setLetter(answerDto.getLetter());
                   return answer;
               }).orElseThrow(()-> new RuntimeException("Answer not found with ID: " + answerDto.getId()));
           }
        }).toList();

//       currentAnswers.removeIf(existingAnswer -> updatedAnswers.stream().noneMatch(updatedAnswer -> updatedAnswer.getId()!=null && updatedAnswer.getId().equals(existingAnswer.getId())));

       question.setAnswers(updatedAnswers);

        questionRepository.save(q);
    }
}
