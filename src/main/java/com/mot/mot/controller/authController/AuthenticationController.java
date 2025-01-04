package com.mot.mot.controller.authController;

import com.mot.mot.authService.AuthenticationService;
import com.mot.mot.model.AuthenticationRequest;
import com.mot.mot.model.AuthenticationResponse;
import com.mot.mot.model.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    @Validated
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest registerRequest,
            HttpServletResponse response
    ){
        return
                ResponseEntity.ok(authenticationService.register(registerRequest,response));
    }
    AtomicInteger total = new AtomicInteger();
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ){
        //date time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        String strDate = now.format(formatter);
        total.incrementAndGet();
        System.out.println("Current time: " + strDate);

        System.out.println("Total request: " + total);
        return
                ResponseEntity.ok(authenticationService.authenticate(authenticationRequest, response));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
