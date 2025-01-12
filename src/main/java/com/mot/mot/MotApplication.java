package com.mot.mot;


import com.mot.mot.authService.AuthenticationService;
import com.mot.mot.helper.DocumentReader;
import com.mot.mot.model.RegisterRequest;
import com.mot.mot.repository.EmbedImageRepository;
import com.mot.mot.service.ImageService;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@RequiredArgsConstructor
public class MotApplication {

	private final AuthenticationService authenticationService;
	private final EmbedImageRepository embedImageRepository;
	private final ImageService imageService;

	private final DocumentReader documentReader;

	public static void main(String[] args) {
		SpringApplication.run(MotApplication.class, args);
	}

	//on application started
//	@EventListener(ApplicationReadyEvent.class)
//	public void onApplicationReadyEvent() throws Exception {
//		authenticationService.registerForTest(
//				RegisterRequest.builder().email("teacher@gmail.com").fullName("TEACHER").role("Teacher").password(
//						"aZ230902@").build()
//		);
//
//		authenticationService.registerForTest(
//				RegisterRequest.builder().email("student@gmail.com").fullName("STUDENT").role("Student").password(
//						"aZ230902@").build()
//		);
//
//
//	}


}

