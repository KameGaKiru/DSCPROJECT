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

                // PÚBLICO — login e cadastro
                .requestMatchers(
                        "/api/usuario/cadastrar",
                        "/api/usuario/login"
                ).permitAll()

                // CHECKLIST — motorista e coordenador
                .requestMatchers("/api/checklist/**")
                        .hasAnyRole("MOTORISTA", "COORDENADOR")

                // LISTAR USUÁRIOS — motorista e coordenador
                .requestMatchers("/api/usuario/listar")
                        .hasAnyRole("MOTORISTA", "COORDENADOR")

                // LISTAR VEÍCULOS — motorista e coordenador
                // (necessário para popular o select no checklist)
                .requestMatchers("/api/veiculo/listar")
                        .hasAnyRole("MOTORISTA", "COORDENADOR")

                // GERENCIAR VEÍCULOS (cadastrar/editar/deletar) — só coordenador
                .requestMatchers(
                        "/api/veiculo/cadastrar",
                        "/api/veiculo/atualizar/**",
                        "/api/veiculo/deletar/**",
                        "/api/veiculo/buscar/**"
                ).hasRole("COORDENADOR")

                // GERENCIAR USUÁRIOS — só coordenador
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