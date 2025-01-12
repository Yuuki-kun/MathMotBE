package com.mot.mot.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostResponse {
    private String title;
    private String content;
    private Long authorUserId;
    private Long parentClassId;

    private Long id;

    private List<String> fileUrls;
}
