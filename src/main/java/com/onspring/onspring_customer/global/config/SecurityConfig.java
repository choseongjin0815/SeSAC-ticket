package com.onspring.onspring_customer.global.config;


import com.onspring.onspring_customer.global.util.jwt.JwtTokenProvider;
import com.onspring.onspring_customer.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인을 위한 경로는 모두 허용
                        .requestMatchers("/api/franchise/login", "/api/user/login", "/api/token/refresh","/error", "/api/franchise/menu/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // user, franchise 경로에 필터 적용
                        // 단순 authenticated()하면 가맹점으로 로그인 시에도 사용자 경로도 사용가능
                        .requestMatchers("/api/franchise/**").hasRole("FRANCHISE")
                        .requestMatchers("/api/user/**").hasRole("USER")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}