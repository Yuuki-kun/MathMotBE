package com.mot.mot.service;

import com.mot.mot.model.dto.CreateExamResponse;
import com.mot.mot.model.dto.ExamInfoDto;
import com.mot.mot.model.entity.Exam;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface IExamService extends ICrudService<Exam> {
    CreateExamResponse readExam(MultipartFile file) throws Exception;

    Page<ExamInfoDto> findAllOrderByDateDesc(Pageable pageable);

    List<Exam> findExamsStartingSoon();

    ExamInfoDto getFullExamInfo(Long id);

    Integer updateExamInfo(Long id, ExamInfoDto examInfoDto) throws BadRequestException;

    Page<ExamInfoDto> findAllPageableByStudentId(Long studentId, Pageable pageable);

    List<ExamInfoDto> findExamsStartingSoonByStudentId(Long studentId, int minutesLater);
}
