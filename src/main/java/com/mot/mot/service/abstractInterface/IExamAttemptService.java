package com.mot.mot.service.abstractInterface;

import com.mot.mot.model.dto.ExamAttemptDto;
import com.mot.mot.model.entity.ExamAttempt;

import java.util.List;

public interface IExamAttemptService extends ICrudService<ExamAttempt> {
    List<ExamAttemptDto> findAllByExamIdAndUserId(Long examId, Long userId);
}
