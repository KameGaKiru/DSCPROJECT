package br.edu.ifpe.dsc.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desativa CSRF para facilitar testes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                "/api/usuario/cadastrar", 
                "/api/usuario/listar", 
                "/api/usuario/atualizar/**",
                "/api/usuario/buscar/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.disable()) // desativa formulário de login
            .httpBasic(); // ativa autenticação básica HTTP (opcional para testes)

        return http.build();
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}