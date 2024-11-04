package com.mot.mot.model.request;

import com.mot.mot.model.dto.ClassDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequest {

    @NotNull(message = "Thông tin lớp không được rỗng")
    @Valid
    private ClassDto classDto;

    @NotNull(message = "Teacher ID không được trống")
    @Min(value = 0, message = "Teacher ID phải lớn hơn hoặc bằng 0")
    private Long teacherId;

}
