package com.mot.mot.model.dto;

import com.mot.mot.model.enums.ExamType;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamInfoDto {
    private Long id;

    private String title;
    private String description;

    private Boolean published;

    private Date startDate;

    private Date endDate;

    private Date createdDate;

    private Long timeLimit;

    private Integer retakeLimit;

    private String note;

    private Long assignedClassId;

    private List<QuestionDto> questions;

    private String assignedClassName;

    private ExamType examType;
    private Long autoCloseAfter;

    public ExamInfoDto(Long id, String title, String description, Boolean published, Date startDate, Date endDate,
                       Date createdDate, Long timeLimit, Integer retakeLimit, String note, Long assignedClassId,
                       ExamType examType, Long autoCloseAfter) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.published = published;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
        this.timeLimit = timeLimit;
        this.retakeLimit = retakeLimit;
        this.note = note;
        this.assignedClassId = assignedClassId;
        this.examType = examType;
        this.autoCloseAfter = autoCloseAfter;
    }

    public ExamInfoDto(Long id, String title, String description, Boolean published, Date startDate, Date endDate,
                       Date createdDate, Long timeLimit, Integer retakeLimit, String note, Long assignedClassId,
                       String assignedClassName, ExamType examType,
                       Long autoCloseAfter) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.published = published;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
        this.timeLimit = timeLimit;
        this.retakeLimit = retakeLimit;
        this.note = note;
        this.assignedClassId = assignedClassId;
        this.assignedClassName = assignedClassName;
        this.examType = examType;
        this.autoCloseAfter = autoCloseAfter;
    }

}
