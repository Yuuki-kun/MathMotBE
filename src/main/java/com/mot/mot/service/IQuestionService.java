package com.mot.mot.service;

import com.mot.mot.model.entity.Question;

import java.util.List;

public interface
IQuestionService extends ICrudService<Question> {
    List<Question> getQuestions(int offset, int size, long examId);
}
