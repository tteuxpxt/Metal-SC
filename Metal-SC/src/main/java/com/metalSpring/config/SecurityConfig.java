package com.metalSpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") // Ignora CSRF no H2
                        .disable() // Desabilita geral para API (React gerencia isso)
                )

                // 3. LIBERAR FRAMES (Crucial para o H2 Console aparecer)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // 4. REGRAS DE ACESSO (O "Porteiro")
                .authorizeHttpRequests(auth -> auth
                        // -- Área Pública --
                        .requestMatchers("/h2-console/**").permitAll()      // Banco de Dados
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Criar conta
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()    // Logar
                        .requestMatchers(HttpMethod.GET, "/api/pecas/**").permitAll()  // Ver peças (vitrine)

                        // -- Área Restrita (Todo o resto precisa de login) --
                        .anyRequest().authenticated()
                )

                // 5. TIPO DE LOGIN (Básico para testes, pode evoluir para JWT depois)
                .httpBasic(basic -> {});

        return http.build();
    }

    // Apague ou comente o @Bean do BCryptPasswordEncoder que você tem e coloque este:

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

        // Permite requisições do seu Frontend (ajuste a porta se usar Vite/Next)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));

        // Permite os métodos HTTP comuns
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permite cabeçalhos (Auth, Content-Type, etc)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite enviar cookies/credenciais se necessário
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}