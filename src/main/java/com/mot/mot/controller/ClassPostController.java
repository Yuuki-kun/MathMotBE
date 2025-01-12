package com.mot.mot.controller;

import com.mot.mot.model.User;
import com.mot.mot.model.dto.ClassPostDto;
import com.mot.mot.model.dto.CreatePostDto;
import com.mot.mot.model.dto.UploadImageResponse;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.ClassPost;
import com.mot.mot.model.entity.EmbedImage;
import com.mot.mot.repository.EmbedImageRepository;
import com.mot.mot.service.abstractInterface.IClassPostService;
import com.mot.mot.service.abstractInterface.IClassService;
import com.mot.mot.service.abstractInterface.IImageUpload;
import com.mot.mot.service.LocalStoreImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("class-posts")
@RequiredArgsConstructor
@Slf4j
public class ClassPostController {
    private final IClassService classService;
    private final IClassPostService classPostService;
    private IImageUpload imageUpload = new LocalStoreImageService();

    private final EmbedImageRepository embedImageRepository;



    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getClassPosts(@PathVariable Long classId, Pageable pageable) {

        List<ClassPost> classPosts = classPostService.findByClassIdPageable(classId, pageable);

        List<ClassPostDto> classPostDtos = classPosts.stream().map(
                classPost -> ClassPostDto.builder()
                        .id(classPost.getId())
                        .title(classPost.getTitle())
                        .content(classPost.getContent())
                        .authorUserId(classPost.getUser().getId())
                        .parentClassId(classId)
                        .authorName(classPost.getUser().getFullName())
                        .authorProfileImageUrl(
                                classPost.getUser().getProfileImage() != null ?
                classPost.getUser().getProfileImage().getUrl():
                                        ""
                        )
                        .likeCount(classPost.getLikeCount())
                        .imageUrls(embedImageRepository.findUrlsByClassPostId(classPost.getId()))
                        .build()
        ).toList();

        return ResponseEntity.ok(classPostDtos);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createClassPost(@RequestPart CreatePostDto createPostDto,
                                             @RequestPart(required = false) List<MultipartFile> files) {

        ClassPost classPost = ClassPost.builder()
                .title(createPostDto.getTitle())
                .content(createPostDto.getContent())
                .parentClass(Class.builder().id(createPostDto.getParentClassId()).build())
                .user(User.builder().id(createPostDto.getAuthorUserId()).build())
                .likeCount(0)
                .build();
        ClassPost savedClassPost = classPostService.create(classPost);

        if(files!=null&& !files.isEmpty()){
            files.forEach(
                    file -> {
                        try {
                            String fileName = "file_"+ System.currentTimeMillis() + file.getOriginalFilename();
                            UploadImageResponse u =  imageUpload.upload(fileName, file.getBytes());

                            embedImageRepository.save(EmbedImage.builder().type(u.getType()).name(u.getName()).storageWeb("local").uploaded(true)
                                    .deleteHash(u.getDeleteHash())
                                    .relatedTable("class_post")
                                    .relatedId(savedClassPost.getId())
                                    .url(u.getUrl())
                                    .build());

                        } catch (Exception e) {
                            log.error("Error uploading image", e);
                        }
                    }
            );
        }

        return ResponseEntity.ok(200);
    }

}
