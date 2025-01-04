package com.mot.mot.controller.teacher.authenticatedController;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.service.ClassService;
import com.mot.mot.service.IClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("teacher/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassesController {

//    private final IClassService classService;
//
//    @PostMapping
//    @Validated
//    public ResponseEntity<?> createClass(@RequestBody @Valid CreateClassRequest createClassRequest) {
//
//        if (createClassRequest == null || createClassRequest.getClassDto() == null) {
//            log.error("Invalid request body");
//            log.error("At :", this.getClass().getName());
//            log.error("Method : createClass");
//            log.error("Request body :", createClassRequest);
//            throw new CustomBadRequestException("Invalid request body");
//        }
//
//        Class savedClass = classService.create(createClassRequest);
//        return ResponseEntity.ok(savedClass);
//    }
}
