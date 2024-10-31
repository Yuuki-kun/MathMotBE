package com.mot.mot.controller.teacher.authenticatedController;

import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createClass(@RequestBody CreateClassRequest createClassRequest){
        var toSaveClass = Class.builder().className(createClassRequest.getClassDto().getClassName())
                .classDesc(createClassRequest.getClassDto().getClassDesc())
                .classGrade(createClassRequest.getClassDto().getClassGrade())
                .classStatus(createClassRequest.getClassDto().getClassStatus())
                .createdAt(createClassRequest.getClassDto().getCreatedAt())
                .teacher(Teacher.builder().teacherId(createClassRequest.getTeacherId()).build()).build();
        var savedClass = classService.create(toSaveClass);
        return ResponseEntity.ok(savedClass);
    }


}
