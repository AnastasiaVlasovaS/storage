package com.example.cloud.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()   // открытые эндпоинты
                        .anyRequest().authenticated()                // всё остальное требует аутентификации
                )
                .formLogin(form -> form          // форма логина
                        .loginPage("/login")         // можно кастомную страницу
                        .permitAll()
                )
                .httpBasic(withDefaults());      // или базовая HTTP-аутентификация
        return http.build();
    }

    // Пример пользователя в памяти для тестов
    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("{noop}password")   // {noop} означает plain text (только для тестов!)
                        .roles("USER")
                        .build()
        );
    }
}