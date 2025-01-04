package com.mot.mot.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponse {
    private String name;
    private String url;
    private String storageWeb;
    private String deleteHash;
    private String type;
    private Long size;
}
