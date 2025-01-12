package com.mot.mot.service;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.errorHandler.CustomNotFoundException;
import com.mot.mot.helper.DocumentReader;
import com.mot.mot.model.dto.AnswerDto;
import com.mot.mot.model.dto.CreateExamResponse;
import com.mot.mot.model.dto.ExamInfoDto;
import com.mot.mot.model.dto.QuestionDto;
import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.Exam;
import com.mot.mot.repository.ClassRepository;
import com.mot.mot.repository.ExamRepository;
import com.mot.mot.service.abstractInterface.IExamService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ExamService extends CrudServiceImpl<Exam> implements IExamService {
    private final DocumentReader documentReader;
    private final ExamRepository examRepository;
    private final ClassRepository classRepository;
    @Autowired
    public ExamService(ExamRepository examRepository, DocumentReader documentReader, ClassRepository classRepository) {
        super(examRepository);
        this.examRepository = examRepository;
        this.documentReader = documentReader;
        this.classRepository = classRepository;
    }

    public CreateExamResponse readExam(MultipartFile file) throws Exception {
        return documentReader.readDocument(file);
    }

    @Override
    public Page<ExamInfoDto> findAllOrderByDateDesc(Pageable pageable) {
        return examRepository.findAllByOrderByDateDesc(pageable);
    }

    @Override
    public List<Exam> findExamsStartingSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
        return examRepository.findExamsStartingSoon(now, thirtyMinutesLater);
    }

    @Override
    public ExamInfoDto getFullExamInfo(Long id) {
        Exam e = examRepository.findById(id).orElseThrow(()-> new CustomNotFoundException("Exam with id "+id+" not found"));
        ExamInfoDto examInfoDto = new ExamInfoDto(e.getId(), e.getTitle(), e.getDescription(), e.getPublished(),
                e.getStartDate(), e.getEndDate(), e.getCreatedDate(), e.getTimeLimit(), e.getRetakeLimit(),
                e.getNote(), e.getAssignedClass() == null ? null : e.getAssignedClass().getId(), e.getExamType(), e.getAutoCloseAfter());
        examInfoDto.setQuestions(e.getQuestions().stream().map(q -> {
            List<AnswerDto> answers = new ArrayList<>();
            for (Answer a : q.getAnswers()) {
                AnswerDto answerDto = AnswerDto.builder().id(a.getId()).content(a.getContent()).correct(a.getCorrect()).letter(a.getLetter()).build();
                answers.add(answerDto);
            }
            return QuestionDto.builder().title(q.getTitle()).type(q.getType()).point(q.getPoint()).level(q.getLevel()).id(q.getId()).answers(answers).build();
        }).toList());
        return examInfoDto;
    }

    @Override
    public Integer updateExamInfo(Long id, ExamInfoDto examInfoDto) throws BadRequestException {

        Exam exam = examRepository.findById(id).orElseThrow(()-> new CustomBadRequestException("Exam with id "+id+" not found"));
        exam.setTitle(examInfoDto.getTitle());
        exam.setDescription(examInfoDto.getDescription());
        exam.setPublished(examInfoDto.getPublished());
        exam.setStartDate(examInfoDto.getStartDate());
        exam.setEndDate(examInfoDto.getEndDate());
        exam.setTimeLimit(examInfoDto.getTimeLimit());
        exam.setRetakeLimit(examInfoDto.getRetakeLimit());
        exam.setNote(examInfoDto.getNote());

        if(examInfoDto.getAssignedClassId() != null && examInfoDto.getAssignedClassId() > 0){
            exam.setAssignedClass(classRepository.findById(examInfoDto.getAssignedClassId()).orElseThrow(()-> new CustomBadRequestException("Class with id "+examInfoDto.getAssignedClassId()+" not found")));
        }

        exam.setExamType(examInfoDto.getExamType());
        examRepository.save(exam);

        return exam.getId() > 0 ? 1 : 0;
    }

    @Override
    public Page<ExamInfoDto> findAllPageableByStudentId(Long studentId, Pageable pageable) {
        return examRepository.findAllPageableByStudentId(studentId, pageable);
    }

    @Override
    public List<ExamInfoDto> findExamsStartingSoonByStudentId(Long studentId, int minutesLater) {
        List<ExamInfoDto> exams = findAllPageableByStudentId(studentId, Pageable.unpaged()).getContent();


        if(!exams.isEmpty()){
          LocalDateTime now = LocalDateTime.now();
          LocalDateTime minutesLaterTime = now.plusMinutes(minutesLater);

          //print use ::

          return exams.stream().filter(
                  e -> e.getStartDate() != null &&
                          toLocalDateTime(e.getStartDate()).isAfter(now) && toLocalDateTime(e.getStartDate()).isBefore(minutesLaterTime)
          ).collect(toList());
        }
        return List.of();
    }


    @Override
    public Exam create(Object object) {
        return null;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


}


