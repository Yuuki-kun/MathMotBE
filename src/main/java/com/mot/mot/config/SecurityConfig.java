package com.mot.mot.config;

import com.mot.mot.model.Role;
import com.mot.mot.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

//                .cors(cors -> {
//                    cors.configurationSource(request -> {
//                        var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
//                        corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
//                        corsConfiguration.setAllowedMethods(java.util.List.of(GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name()));
//                        corsConfiguration.setAllowedHeaders(java.util.List.of("Authorization", "Cache-Control", "Content-Type"));
//                        return corsConfiguration;
//                    });
//                })

                .csrf(AbstractHttpConfigurer::disable)
//                .exceptionHandling(
//                        exceptionHandlingConfigurer -> exceptionHandlingConfigurer
//                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                                .accessDeniedHandler((request, response, accessDeniedException) -> {
//                                    response.setContentType("application/json");
//                                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//                                    response.sendError(403, "Access Denied");
//
//                                })
//
//                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Set 401 status code
                            response.getWriter().write("Unauthorized: " + authException.getMessage());  // Optional message
//                            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                            response.setHeader("Access-Control-Allow-Credentials", "true");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // Set 403 status code
                            response.getWriter().write("Forbidden: " + accessDeniedException.getMessage());  // Optional message
//                            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                            response.setHeader("Access-Control-Allow-Credentials", "true");
                        })
                )                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .requestMatchers(OPTIONS).permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/test/**").permitAll()
                                .requestMatchers("/authentication/**").permitAll()
                                .requestMatchers("/ws/**").permitAll()

                                .requestMatchers("/question/**").hasRole(Role.ADMIN.name())
                                .requestMatchers("/teacher/**").hasRole(Role.ADMIN.name())
                                .requestMatchers("/classes/student/**").hasRole(Role.USER.name())

                                .requestMatchers("/examAttempt/student/**").hasRole(Role.USER.name())
                                .anyRequest().authenticated()
                )

//                .oauth2Login(oauth2Login -> oauth2Login
//                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
//                                .userService(new CustomOAuth2UserService(userRepository))
//                        )
//                        .successHandler((request, response, authentication) -> {
//                            response.sendRedirect("http://localhost:3000");
//                        })
//                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer
                                .logoutUrl("/authentication/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext()));

        return http.build();
    }
}
