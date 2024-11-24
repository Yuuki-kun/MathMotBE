package com.mot.mot.controller.teacher.authenticatedController;

import com.mot.mot.model.entity.Question;
import com.mot.mot.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("teacher/exams")
@RequiredArgsConstructor
@Slf4j
public class ExamController {
    private final ExamService examProcessorService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadExam(@RequestParam("file") MultipartFile file) {
        return null;

    }
}
