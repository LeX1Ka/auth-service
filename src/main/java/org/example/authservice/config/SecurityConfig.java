package org.example.authservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable());


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
