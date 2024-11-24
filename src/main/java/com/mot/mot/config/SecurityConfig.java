package com.mot.mot.config;

import com.mot.mot.model.Role;
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

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
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
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .requestMatchers("/test/**").permitAll()
                                .requestMatchers("/authentication/**").permitAll()
                                .requestMatchers("/teacher/**").hasRole(Role.ADMIN.name())

                                .anyRequest().authenticated()
                )
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
