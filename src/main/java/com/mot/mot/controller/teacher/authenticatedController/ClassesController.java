package com.mot.mot.controller.teacher.authenticatedController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
