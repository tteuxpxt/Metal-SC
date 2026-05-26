package com.metalSpring;

import com.metalSpring.model.entity.Administrador;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class MetalScApplication {

	private static final Logger logger = LoggerFactory.getLogger(MetalScApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MetalScApplication.class, args);
	}

	@Bean
	public CommandLineRunner adminSeeder(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			@Value("${app.admin.enabled:true}") boolean adminEnabled,
			@Value("${app.admin.email:admin@metal.com}") String adminEmail,
			@Value("${app.admin.password:admin123}") String adminPassword,
			@Value("${app.admin.nome:Administrador}") String adminNome
	) {
		return args -> {
			if (!adminEnabled) {
				return;
			}

			if (adminEmail == null || adminEmail.isBlank()
					|| adminPassword == null || adminPassword.isBlank()) {
				return;
			}

			try {
				if (usuarioRepository.existsByEmail(adminEmail)) {
					return;
				}

				Administrador admin = new Administrador(
						adminNome,
						adminEmail,
						passwordEncoder.encode(adminPassword)
				);
				admin.setAtivo(true);

				usuarioRepository.save(admin);
			} catch (Exception e) {
				logger.warn("Admin seed ignorado: {}", e.getMessage());
			}
		};
	}
}
