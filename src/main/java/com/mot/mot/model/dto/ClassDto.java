package com.mot.mot.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.enums.ClassStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto {
    private Long id;

    @NotNull(message = "Tên lớp không được trống")
    @Size(min = 1, max = 100, message = "Tên lớp giới hạn từ 1 đến 100 ký tự")
    private String className;

    @NotNull(message = "Class grade không được trống")
    @Min(value = 1, message = "Class grade phải lớn hơn 0")
    private Long classGrade;

    @NotNull(message = "Trạng thái lớp không được trống")
    //type as string when json
    private ClassStatus classStatus;

    @Size(max = 500, message = "Mô tả lớp không vượt quá 500 ký tự")
    private String classDesc;

    @PastOrPresent(message = "Ngày tạo lớp phải ở quá khứ hoặc hiện tại")
    private Date createdAt;

}
