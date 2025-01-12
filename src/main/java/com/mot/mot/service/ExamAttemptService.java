package com.mot.mot.service;

import com.mot.mot.model.entity.ExamAttempt;
import com.mot.mot.repository.ExamAttemptRepository;
import com.mot.mot.service.abstractInterface.IExamAttemptService;
import org.springframework.stereotype.Service;

@Service
public class ExamAttemptService extends CrudServiceImpl<ExamAttempt> implements IExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    public ExamAttemptService(ExamAttemptRepository examAttemptRepository) {
        super(examAttemptRepository);
        this.examAttemptRepository = examAttemptRepository;
    }

    @Override
    public ExamAttempt create(Object object) {
        if(object instanceof ExamAttempt) {
            return examAttemptRepository.save((ExamAttempt) object);
        }
        throw new IllegalArgumentException("Object is not an instance of ExamAttempt");
    }
}
