package com.saasdemo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;



@SpringBootApplication()
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableScheduling
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT" 
)
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth"),
		info = @Info(
					title = "ETAT CIVIL REST API Documentation",
					description="Documentation interactive de l'API de gestion des naissances, mariages, décès, etc. ",
					version = "V1",
					contact = @Contact(
						name = "ACHO YATTE DEIVY CONSTANT",
						email = "acho.quebec@gmail.com",
						url = "https://github.com/ACHOYATTE2025"
					),
					license = @License(
						name = "apache 2.0",
						url="https://github.com/ACHOYATTE2025"
					)
			),
		externalDocs = @ExternalDocumentation(
								description = "Documentation interactive de l'API de gestion des naissances, mariages, décès, etc. ",
								url = "https://github.com/ACHOYATTE2025"
		)
)


public class SaasBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasBackendApplication.class, args);
    }

}
