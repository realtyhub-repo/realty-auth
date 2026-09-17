package service.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecureRandom secureRandom(){return new SecureRandom();}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))  // 👈 esto es lo que faltaba
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
