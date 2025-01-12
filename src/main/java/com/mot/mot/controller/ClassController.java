package com.mot.mot.controller;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.dto.UploadImageResponse;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.EmbedImage;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.repository.EmbedImageRepository;
import com.mot.mot.service.abstractInterface.IClassService;
import com.mot.mot.service.abstractInterface.IImageUpload;
import com.mot.mot.service.LocalStoreImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {
    private final IClassService classService;
    private final IImageUpload imageUpload = new LocalStoreImageService();
    private final EmbedImageRepository embedImageRepository;

    @GetMapping
    public ResponseEntity<ClassDto> getClassById(@RequestParam Long classId) {
        return ResponseEntity.ok(classService.findClassDtoById(classId));
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Validated
    public ResponseEntity<?> createClass(@RequestPart("createClassRequest") @Valid CreateClassRequest createClassRequest,
                                         @RequestPart("classAvatarFile") MultipartFile file) throws IOException {



        if (createClassRequest == null || createClassRequest.getClassDto() == null) {
            log.error("Invalid request body");
            log.error("At :", this.getClass().getName());
            log.error("Method : createClass");
            log.error("Request body :", createClassRequest);
            throw new CustomBadRequestException("Invalid request body");
        }



        Class savedClass = classService.create(createClassRequest);
        EmbedImage embedImage = null;
        if(!file.isEmpty() && savedClass != null) {
            //create new file name
            String fileName =
                    createClassRequest.getClassDto().getClassName() + System.currentTimeMillis() + file.getOriginalFilename();
            UploadImageResponse uploadImageResponse = imageUpload.upload(fileName, file.getBytes());

             embedImage = EmbedImage.builder()
                    .uploaded(true)
                    .relatedId(savedClass.getId())
                    .relatedTable("class")
                    .deleteHash(uploadImageResponse.getDeleteHash())
                    .name(uploadImageResponse.getName())
                    .storageWeb(uploadImageResponse.getStorageWeb())
                    .type(uploadImageResponse.getType())

                    .url(uploadImageResponse.getUrl())
                    .build();
            embedImageRepository.save(embedImage);
        }

        ClassDto savedClassDto =
                ClassDto.builder().id(savedClass.getId()).classDesc(savedClass.getClassDesc()).classStatus(savedClass.getClassStatus())
                        .classGrade(savedClass.getClassGrade())
                        .className(savedClass.getClassName()).createdAt(savedClass.getCreatedAt())
                        .imageUrl(embedImage != null ? embedImage.getUrl() : "")
                        .build();
        return ResponseEntity.ok(savedClassDto);
    }

    //find student enrolled classes
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<Class>> getClassesByStudentId(@PathVariable Long studentId, Pageable pageable) {
        return ResponseEntity.ok(classService.findAllByStudentId(studentId, pageable));
    }

    //find classes by class name
    @GetMapping("/class-name/{className}/{studentId}")
    public ResponseEntity<?> getClassesByClassName(@PathVariable String className, @PathVariable Long studentId) {
        return ResponseEntity.ok(classService.findAllByClassName(className, studentId));
    }

    //find all classes by teacher id
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getClassesByTeacherId(@PathVariable Long teacherId, Pageable pageable) {
        return ResponseEntity.ok(classService.findAllByTeacherId(teacherId, pageable));
    }
}
