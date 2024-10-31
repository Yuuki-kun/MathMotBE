package com.mot.mot.model.request;

import com.mot.mot.model.dto.ClassDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequest {
    private ClassDto classDto;
    private Long teacherId;
}
