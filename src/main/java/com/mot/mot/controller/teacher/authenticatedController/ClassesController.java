package com.mot.mot.controller.teacher.authenticatedController;

import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("teacher/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassService classService;

    @PostMapping
    @Validated
    public ResponseEntity<?> createClass(@RequestBody @Valid CreateClassRequest createClassRequest) {

        if (createClassRequest == null || createClassRequest.getClassDto() == null) {
            return ResponseEntity.badRequest().body("Invalid input");
        }

        Class savedClass = classService.create(createClassRequest);
        return ResponseEntity.ok(savedClass);
    }
}
