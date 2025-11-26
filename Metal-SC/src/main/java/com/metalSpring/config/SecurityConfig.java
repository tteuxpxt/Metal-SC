package com.metalSpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CONFIGURAÇÃO DE CORS (Permite o React acessar o Java)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. DESABILITAR CSRF (Necessário para APIs REST e H2 Console)
                .csrf(csrf -> csrf.disable())

                // 3. LIBERAR FRAMES (Crucial para o H2 Console aparecer)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 4. PERMITIR TUDO - SEM RESTRIÇÕES (APENAS PARA DESENVOLVIMENTO!)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // PERMITE TUDO SEM AUTENTICAÇÃO
                );

        return http.build();
    }

    // ⚠️ ATENÇÃO: NoOpPasswordEncoder está DEPRECATED e é INSEGURO!
    // Use apenas para desenvolvimento/testes. Em produção use BCrypt!
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Isso força o Spring a usar senhas em texto puro (Só para desenvolvimento!)
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    // Configuração Detalhada do CORS (React <-> Java)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite requisições de QUALQUER ORIGEM (APENAS DESENVOLVIMENTO!)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Permite os métodos HTTP comuns
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permite todos os cabeçalhos
        configuration.setAllowedHeaders(List.of("*"));

        // Permite enviar cookies/credenciais
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}