package com.metas.meta_financeira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Autorização das rotas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Login com OAuth2 (Google)
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/meta", true) // sempre manda pro /meta
                )
                // Logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // depois de logout, manda pra home
                        .permitAll()
                )
                // CSRF pode ser mantido habilitado
                .csrf(csrf -> csrf.disable()); // se vc for usar só templates Thymeleaf, pode deixar ativado

        return http.build();
    }
}