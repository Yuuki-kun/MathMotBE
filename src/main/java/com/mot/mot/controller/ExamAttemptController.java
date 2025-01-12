package com.mot.mot.controller;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.dto.*;
import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.AnswerAttempt;
import com.mot.mot.model.entity.ExamAttempt;
import com.mot.mot.model.entity.Question;
import com.mot.mot.repository.AnswerAttemptRepository;
import com.mot.mot.repository.ExamAttemptRepository;
import com.mot.mot.repository.ExamRepository;
import com.mot.mot.repository.QuestionRepository;
import com.mot.mot.service.abstractInterface.IExamAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/examAttempt")
@RequiredArgsConstructor
@Slf4j
public class ExamAttemptController {

    private final IExamAttemptService examAttemptService;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerAttemptRepository  answerAttemptRepository;
    private final QuestionRepository questionRepository;

    @PostMapping("/student/submit")
    public ResponseEntity<?> submitExamAttempt(@RequestBody AnswerAttemptDto answerAttemptDto) {

        ExamAttempt examAttempt = examAttemptRepository.findById(answerAttemptDto.getExamAttemptId())
                .orElseThrow(() -> new CustomBadRequestException("Exam Attempt Not Found"));
        if(examAttempt.getFinished()){
            throw new CustomBadRequestException("Exam Attempt Already Finished");
        }

        List<AnswerAttempt> answerAttempts = new ArrayList<>();

         List<QuestionDto> questionDtos = questionRepository.getQuestionsByOffsetAndSizeAndExamId(0,999,
                answerAttemptDto.getExamId()).stream().map(
                question -> {
                    List<AnswerDto> answers = question.getAnswers().stream().map(
                            answer -> AnswerDto.builder()
                                    .id(answer.getId())
                                    .content(answer.getContent())
                                    .correct(answer.getCorrect())
                                    .letter(answer.getLetter())
                                    .build()
                    ).toList();
                    return QuestionDto.builder()
                            .id(question.getId())
                            .answers(answers)
                            .point(question.getPoint())
                            .orderNumber(question.getOrderNumber())
                            .build();
                }
         ).toList();

        float totalPoint = 0;
        int numberOfCorrectAnswer = 0;
        int numberOfWrongAnswer = 0;
        int numberOfNotAnswer = 0;
        List<AnswerAttemptResultResponse> answerAttemptResultResponses = new ArrayList<>();

        //self question map
        Map<Long, QuestionDto> questionDtoMap = questionDtos.stream().collect(Collectors.toMap(QuestionDto::getId, Function.identity()));

        for(QuestionDto questionDto : questionDtos){

            //question answer by user
            QuestionAnswering userAnswer = answerAttemptDto.getQuestionAnswerings().stream()
                    .filter(questionAnswering1 -> questionAnswering1.getQuestionId().equals(questionDto.getId()))
                    .findFirst()
                    .orElse(null);
            AnswerDto correctAnswer = questionDto.getAnswers().stream()
                    .filter(AnswerDto::getCorrect)
                    .findFirst()
                    .orElse(null);

            boolean correct = false;
            String selectedAnswerLetter = null;
            Long selectedAnswerId = null;

            if(userAnswer != null){
                selectedAnswerId = userAnswer.getAnswerId();
                AnswerDto selectedAnswer = questionDto.getAnswers().stream()
                        .filter(answerDto -> answerDto.getId().equals(userAnswer.getAnswerId()))
                        .findFirst()
                        .orElse(null);

                if(selectedAnswer != null){
                    selectedAnswerLetter = selectedAnswer.getLetter();

                    AnswerAttempt answerAttempt = AnswerAttempt.builder()
                            .question(Question.builder().id(questionDto.getId()).build())
                            .answer(Answer.builder().id(selectedAnswerId).build())
                            .examAttempt(examAttempt)
                            .build();
                    if(Boolean.TRUE.equals(selectedAnswer.getCorrect())) {
                        answerAttempt.setCorrect(true);
                        answerAttempt.setPoint(questionDto.getPoint());
                        correct = true;
                        totalPoint += questionDto.getPoint();
                        numberOfCorrectAnswer++;
                    }else{
                        answerAttempt.setCorrect(false);
                        answerAttempt.setPoint(0f);
                        numberOfWrongAnswer++;
                    }

                    answerAttempts.add(answerAttempt);

                }else{

                        numberOfNotAnswer++;
                }
            }else {
                numberOfNotAnswer++;
            }

            answerAttemptResultResponses.add(
                    AnswerAttemptResultResponse.builder()
                            .questionId(questionDto.getId())
                            .questionOrderNumber(questionDto.getOrderNumber())
                            .selectedAnswerId(selectedAnswerId)
                            .selectedAnswerLetter(selectedAnswerLetter == null ? "Skipped" : selectedAnswerLetter)
                            .correctAnswerLetter(correctAnswer != null ? correctAnswer.getLetter() : "Not Found")
                            .correct(selectedAnswerId != null ? correct : null)
                            .correctAnswerDetails(correctAnswer != null ? correctAnswer.getContent() : "Not Found")
                            .typeOfQuestion(questionDto.getType())
                            .point(correct ? questionDto.getPoint() : 0)
                            .build()
            );

        }

        examAttempt.setFinished(true);
        examAttempt.setPoint(totalPoint);

        examAttemptRepository.save(examAttempt);
        answerAttemptRepository.saveAll(answerAttempts);


        return ResponseEntity.ok(
                ExamAttemptResultResponse.builder()
                        .examId(answerAttemptDto.getExamId())
                        .examAttemptId(answerAttemptDto.getExamAttemptId())
                        .totalPoint(totalPoint)
                        .numberOfCorrectAnswer(numberOfCorrectAnswer)
                        .numberOfWrongAnswer(numberOfWrongAnswer)
                        .numberOfNotAnsweredQuestion(numberOfNotAnswer)
                        .answerAttemptResultResponses(answerAttemptResultResponses)
                        .build()
        );
    }
}
