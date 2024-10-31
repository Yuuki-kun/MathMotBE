package com.mot.mot.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestCCC {

    @GetMapping("/test-api")
    public String testApi(){
        return "Test API";
    }
}
