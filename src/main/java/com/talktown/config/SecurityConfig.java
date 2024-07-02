package com.talktown.config;

import com.talktown.util.JwtTokenFilter;
import com.talktown.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger =  LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Security Filter Chain");
        http
                .csrf().disable();
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v[1-5]/register").permitAll()
//                        .requestMatchers("/api/v[1-5]/login").permitAll()
//                        .requestMatchers("/api/v[1-5]/otp").permitAll()
//                        .requestMatchers("/api/v[1-5]/common/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
//                        .requestMatchers("/api/v[1-5]/manager/**").hasRole("MANAGER")
//                        .anyRequest().authenticated()
//                ).addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
