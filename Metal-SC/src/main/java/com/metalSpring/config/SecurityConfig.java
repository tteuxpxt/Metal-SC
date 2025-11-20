package com.metalSpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura o encoder de senha usando BCrypt
     * BCrypt é um algoritmo seguro para hash de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura a cadeia de filtros de segurança
     * Esta configuração é para DESENVOLVIMENTO - ajuste para produção!
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (necessário para APIs REST)
                .csrf(csrf -> csrf.disable())

                // Configura CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configura política de sessão (stateless para APIs REST)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configura autorização de requisições
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - não requerem autenticação
                        .requestMatchers(
                                "/api/auth/**",           // Login, registro, etc
                                "/api/pecas/**",          // Listagem de peças (público)
                                "/api/revendedores/**",   // Listagem de revendedores (público)
                                "/api/search/**",         // Busca de peças
                                "/h2-console/**",         // Console H2 (apenas dev)
                                "/actuator/**",           // Endpoints do Actuator (apenas dev)
                                "/swagger-ui/**",         // Swagger UI
                                "/v3/api-docs/**"         // OpenAPI docs
                        ).permitAll()

                        // Endpoints de administrador
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")

                        // Endpoints de revendedor
                        .requestMatchers(
                                "/api/pedidos/vendedor/**",
                                "/api/pecas/cadastrar",
                                "/api/pecas/atualizar/**"
                        ).hasRole("REVENDEDOR")

                        // Qualquer outra requisição requer autenticação
                        .anyRequest().authenticated()
                )

                // Permite frames (necessário para H2 Console)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    /**
     * Configura CORS para permitir requisições de diferentes origens
     * Ajuste os valores conforme necessário para produção
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (ajuste para produção!)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // React dev server
                "http://localhost:4200",      // Angular dev server
                "http://localhost:8080"       // Mesma origem
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With"
        ));

        // Permite envio de credenciais (cookies, headers de autenticação)
        configuration.setAllowCredentials(true);

        // Tempo de cache da configuração CORS (em segundos)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * CONFIGURAÇÃO ALTERNATIVA PARA DESENVOLVIMENTO
     * Descomente o código abaixo se quiser DESABILITAR toda segurança
     * temporariamente durante o desenvolvimento
     */
    /*
    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // PERMITE TUDO - APENAS DEV!
            );

        return http.build();
    }
    */
}