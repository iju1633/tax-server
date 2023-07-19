package com.tax.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // 보안에 필요한 설정 관리

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // 민감한 정보 암호화/복호화에 사용

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 보안 관련 설정 및 기본 로그인 화면 비활성화

        http.csrf().disable();

        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated();
        http.formLogin().disable();

        return http.build();
    }
}
