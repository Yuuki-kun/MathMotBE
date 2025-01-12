package com.mot.mot.authService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.errorHandler.CustomNotFoundException;
import com.mot.mot.errorHandler.ResourceExistsException;
import com.mot.mot.model.*;
import com.mot.mot.model.entity.Student;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.repository.TokenRepository;
import com.mot.mot.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public void registerForTest(RegisterRequest registerRequest) {

        System.out.println("register="+registerRequest);

        //check if user already exists
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ResourceExistsException("Tài khoản đã tồn tại");
        }

        var user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .locked(false)
                .enabled(true)
                .fullName(registerRequest.getFullName())
                .createdAt(new Date())
                .build();

        if(registerRequest.getRole() != null){
            if(registerRequest.getRole().equals("Student")) {
                var student = Student.builder().build();
                user.setRoles(Set.of(Role.USER));
                user.setStudent(student);
            } else if (registerRequest.getRole().equals("Teacher")) {
                var teacher = Teacher.builder().build();
                user.setRoles(Set.of(Role.ADMIN));
                user.setTeacher(teacher);
            } else {
                throw new CustomBadRequestException("Role không hợp lệ");
            }
        }else {
            throw new CustomBadRequestException("Role không được để trống");
        }

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        var token = Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);


    }


    public AuthenticationResponse register(RegisterRequest registerRequest, HttpServletResponse response) {

        System.out.println("register="+registerRequest);

        //check if user already exists
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ResourceExistsException("Tài khoản đã tồn tại");
        }

        var user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .locked(false)
                .enabled(true)
                .fullName(registerRequest.getFullName())
                .createdAt(new Date())
                .build();

        if(registerRequest.getRole() != null){
            if(registerRequest.getRole().equals("Student")) {
                var student = Student.builder().build();
                user.setRoles(Set.of(Role.USER));
                user.setStudent(student);
            } else if (registerRequest.getRole().equals("Teacher")) {
                var teacher = Teacher.builder().build();
                user.setRoles(Set.of(Role.ADMIN));
                user.setTeacher(teacher);
            } else {
                throw new CustomBadRequestException("Role không hợp lệ");
            }
        }else {
            throw new CustomBadRequestException("Role không được để trống");
        }

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var token = Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

        Cookie refresh_token_cookies = new Cookie("refresh_token", refreshToken);
        refresh_token_cookies.setHttpOnly(true);
        refresh_token_cookies.setSecure(true); //Để 'true' nếu dùng HTTPS
        refresh_token_cookies.setPath("/");
//        refresh_token_cookies.setDomain("localhost");
        refresh_token_cookies.setDomain("https://mathmotbe.onrender.com");
        refresh_token_cookies.setMaxAge(24*60*60);
        response.addCookie(refresh_token_cookies);

        return AuthenticationResponse.builder().accessToken(jwtToken).accessToken(jwtToken)
                .userId(user.getId())
                .memberId(user.getStudent() != null ? user.getStudent().getStudentId() : user.getTeacher().getTeacherId())
                .email(user.getEmail())
                .roles(new ArrayList<>(user.getRoles())).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest,
                                               HttpServletResponse response) {
        System.out.println("user email="+authenticationRequest.getEmail());
        System.out.println("user password="+authenticationRequest.getPassword());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(()-> new CustomNotFoundException("User not found"));
            System.out.println("finf user email = "+user.getEmail());

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);

            var token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(token);

        Cookie refresh_token_cookies = new Cookie("refresh_token", refreshToken);
        refresh_token_cookies.setHttpOnly(true);
        refresh_token_cookies.setSecure(true); //Để 'true' nếu dùng HTTPS
        refresh_token_cookies.setPath("/");
//        refresh_token_cookies.setDomain("localhost");
        refresh_token_cookies.setDomain("https://mathmotbe.onrender.com");
        refresh_token_cookies.setMaxAge(24*60*60);

            response.addCookie(refresh_token_cookies);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .userId(user.getId())
                    .memberId(user.getStudent() != null ? user.getStudent().getStudentId() : user.getTeacher().getTeacherId())
                    .email(user.getEmail())
                    .roles(new ArrayList<>(user.getRoles()))
                    .build();

    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if(userEmail != null){
            var userDetails =
                    this.userRepository.findByEmail(userEmail).orElseThrow();
            if(jwtService.isTokenValid(refreshToken, userDetails)){
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(userDetails);
                var token = Token.builder()
                        .user(userDetails)
                        .token(accessToken)
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .build();
                tokenRepository.save(token);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .userId(userDetails.getId())
                        .email(userDetails.getEmail())
                        .roles(new ArrayList<>(userDetails.getRoles()))
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(),
                        authResponse);
            }
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


}
