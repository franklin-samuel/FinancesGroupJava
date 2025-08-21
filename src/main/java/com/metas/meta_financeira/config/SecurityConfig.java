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
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .requestMatchers("/meta/**").authenticated() // apenas rotas /meta/* precisam de auth
                        .anyRequest().authenticated()
                )
                // Login com OAuth2 (Google)
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/", true) // após login, volta para home
                        .loginPage("/") // página de login customizada é a própria home
                )
                // Logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // depois de logout, manda pra home
                        .permitAll()
                )
                // CSRF desabilitado para simplificar
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}