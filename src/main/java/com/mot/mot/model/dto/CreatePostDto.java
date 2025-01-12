package com.mot.mot.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    private String title;
    private String content;
    private Long authorUserId;
    private Long parentClassId;
}
