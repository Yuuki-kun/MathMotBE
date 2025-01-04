package com.mot.mot.controller.authController.test;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("test-oauth2")
public class TestOAuth2 {
    @GetMapping("/test-fb")
    public String testApi(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        attributes.forEach((k, v) -> {
            System.out.println(k + " : " + v);
        });
        return "Test API" + attributes.get("name");
    }
}
