package com.mot.mot.controller;

import com.mot.mot.model.dto.QuestionDto;
import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.Question;
import com.mot.mot.repository.AnswerRepository;
import com.mot.mot.service.IQuestionService;
import com.mot.mot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final IQuestionService questionService;
    private final AnswerRepository answerRepository;

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable Long id,
                                                       @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                       @RequestParam(value = "size", defaultValue = "5") int size) {
        System.out.println("id = " + id);
        System.out.println("size = " + size);
        System.out.println("offset = " + offset);
        return ResponseEntity.ok(questionService.getQuestions(offset, size, id));

    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) throws BadRequestException {


        Question q = Question.builder()
                .id(questionDto.getId())
                .title(questionDto.getTitle())
                .point(questionDto.getPoint())
                .level(questionDto.getLevel())
                .type(questionDto.getType())
                .answers(questionDto.getAnswers().stream().map(answerDto ->
                    Answer.builder().correct(answerDto.getCorrect()).id(answerDto.getId()).letter(answerDto.getLetter()).content(answerDto.getContent()).build()
                ).toList())
                .build();

        questionService.update(q);
        return ResponseEntity.ok().build();

    }

}
