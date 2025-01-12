package com.mot.mot.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassPostDto {
    private Long id;
    private String title;
    private String content;
    private Long authorUserId;
    private Long parentClassId;

    List<String> imageUrls;

    private String authorName;
    private String authorProfileImageUrl;
    private int likeCount;
}
