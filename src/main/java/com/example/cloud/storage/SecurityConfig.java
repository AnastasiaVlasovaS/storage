package com.example.cloud.storage;

import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Вместо @Autowired + configureGlobal – просто бин UserDetailsService
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return login -> {
            User user = userRepository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/h2-console/**").permitAll()
                        .requestMatchers("/api/files/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Login success\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"Invalid credentials\"}");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logged out\"}");
                        })
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                );

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}