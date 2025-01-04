package com.mot.mot.controller;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.service.IClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {
    private final IClassService classService;

    @GetMapping
    public ResponseEntity<ClassDto> getClassById(@RequestParam Long classId) {
        return ResponseEntity.ok(classService.findClassDtoById(classId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Validated
    public ResponseEntity<?> createClass(@RequestBody @Valid CreateClassRequest createClassRequest) {

        if (createClassRequest == null || createClassRequest.getClassDto() == null) {
            log.error("Invalid request body");
            log.error("At :", this.getClass().getName());
            log.error("Method : createClass");
            log.error("Request body :", createClassRequest);
            throw new CustomBadRequestException("Invalid request body");
        }

        Class savedClass = classService.create(createClassRequest);
        return ResponseEntity.ok(savedClass);
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
