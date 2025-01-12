package com.mot.mot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

@Configuration
@EnableWebMvc
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {
    private static final Long MAX_AGE = 3600L;
    private static final int CORS_FILTER_ORDER = -102;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
       CorsFilter corsFilter = corsFilter();
         FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(corsFilter);
            filterRegistrationBean.setOrder(CORS_FILTER_ORDER);
            return filterRegistrationBean;
    }

    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = getCorsConfiguration();
        source.registerCorsConfiguration("/**", config);
        // Return CorsFilter directly instead of wrapping it in FilterRegistrationBean

        return new CorsFilter(source);
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://192.168.1.30:3000");
        config.addAllowedOrigin("http://192.168.1.26:3000");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://yuuki-kun.github.io");
        config.addAllowedOrigin("https://yuuki-kun.github.io/math-proj/");
        config.addAllowedOrigin("https://math-proj-nine.vercel.app");

        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.OPTIONS.name(),
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        config.setAllowCredentials(true);
        config.setMaxAge(MAX_AGE);

        return config;
    }
}
