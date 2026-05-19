package br.edu.ifpe.dsc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import br.edu.ifpe.dsc.model.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ── PÚBLICO ─────────────────────────────────────────
                .requestMatchers(
                        "/api/usuario/cadastrar",
                        "/api/usuario/login"
                ).permitAll()

                // ── CHECKLIST: cadastrar e listar → motorista + coordenador + mecânico
                .requestMatchers(
                        "/api/checklist/listar",
                        "/api/checklist/cadastrar/**"
                ).hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                // ── CHECKLIST: registrar solução → só mecânico
                .requestMatchers("/api/checklist/solucao/**")
                        .hasAnyRole("MECANICO", "COORDENADOR")

                // ── USUÁRIOS: listar → motorista + coordenador + mecânico
                .requestMatchers("/api/usuario/listar")
                        .hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                // ── VEÍCULOS: listar → motorista + coordenador + mecânico
                .requestMatchers("/api/veiculo/listar")
                        .hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                // ── VEÍCULOS: gerenciar → só coordenador
                .requestMatchers(
                        "/api/veiculo/cadastrar",
                        "/api/veiculo/atualizar/**",
                        "/api/veiculo/deletar/**",
                        "/api/veiculo/buscar/**"
                ).hasRole("COORDENADOR")

                // ── USUÁRIOS: gerenciar → só coordenador
                .requestMatchers(
                        "/api/usuario/atualizar/**",
                        "/api/usuario/deletar/**",
                        "/api/usuario/buscar/**"
                ).hasRole("COORDENADOR")

                .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}