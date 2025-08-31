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
                // público → registrar e login
                .requestMatchers("/api/usuario/cadastrar").permitAll()

                // Motorista pode apenas visualizar lista de usuários
                .requestMatchers("/api/usuario/listar").hasAnyRole("MOTORISTA", "COORDENADOR")

                // Coordenador pode manipular veículos e usuários
                .requestMatchers("/api/veiculo/**").hasRole("COORDENADOR")
                .requestMatchers("/api/usuario/atualizar/**", "/api/usuario/deletar/**").hasRole("COORDENADOR")

                // qualquer outra rota precisa estar logado
                .anyRequest().authenticated()
            )
            .httpBasic(); // HABILITA autenticação Basic (necessário para fetch com Authorization)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
