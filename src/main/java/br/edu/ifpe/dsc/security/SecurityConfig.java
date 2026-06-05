package br.edu.ifpe.dsc.security;

import br.edu.ifpe.dsc.model.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("*"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            }))

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // HTML / FRONT-END PÚBLICO
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/registro.html",
                        "/dashboard_motorista.html",
                        "/dashboard_coordenador.html",
                        "/dashboard_mecanico.html",
                        "/dashboard_checklist.html",
                        "/perfil.html",
                        "/veiculos.html",
                        "/usuarios.html",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico"
                ).permitAll()

                // LOGIN / CADASTRO
                .requestMatchers(
                        "/api/usuario/cadastrar",
                        "/api/usuario/login"
                ).permitAll()

                // CHECKLIST
                .requestMatchers(
                        "/api/checklist/listar",
                        "/api/checklist/cadastrar/**"
                ).hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                .requestMatchers("/api/checklist/solucao/**")
                .hasAnyRole("MECANICO", "COORDENADOR")

                // USUÁRIOS
                .requestMatchers("/api/usuario/listar")
                .hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                .requestMatchers(
                        "/api/usuario/atualizar/**",
                        "/api/usuario/deletar/**",
                        "/api/usuario/buscar/**"
                ).hasRole("COORDENADOR")

                // VEÍCULOS
                .requestMatchers("/api/veiculo/listar")
                .hasAnyRole("MOTORISTA", "COORDENADOR", "MECANICO")

                .requestMatchers(
                        "/api/veiculo/cadastrar",
                        "/api/veiculo/atualizar/**",
                        "/api/veiculo/deletar/**",
                        "/api/veiculo/buscar/**"
                ).hasRole("COORDENADOR")

                // qualquer outra coisa do front pode carregar
                .anyRequest().permitAll()
            )

            .httpBasic(Customizer.withDefaults());

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