package com.saasdemo.backend;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.RoleRepository;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;




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

@AllArgsConstructor
@SpringBootApplication()
public class SaasBackendApplication implements CommandLineRunner{
	private final PasswordEncoder passwordEncoder;
  private final UtilisateurRepository utilisateurRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final CommuneRepository communeRepository;
  private final RoleRepository roleRepository;


 
    public static void main(String[] args) {
        SpringApplication.run(SaasBackendApplication.class, args);
    }

		@Override
		public void run(String... args) throws Exception {
		
			// 1️⃣ Rôle SUPER_ADMIN
Role rolex = this.roleRepository.findByLibele(TypeRole.SUPERADMIN)
        .orElseGet(() -> roleRepository.save(
                Role.builder().libele(TypeRole.SUPERADMIN).build()
        ));

// 2️⃣ Commune Heaven
Area communex = this.communeRepository.findByNameCommune("Heaven")
        .orElseGet(() -> communeRepository.save(
                Area.builder().nameCommune("Heaven").build()
        ));

// 3️⃣ Subscription infinie
Subscription subscriptionx = this.subscriptionRepository.findByUsersName("ACHO")
        .orElseGet(() -> subscriptionRepository.save(
                Subscription.builder()
                        .usersName("ACHO")
                        .amount(15000)
                        .created(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusYears(100))
                        .status(StatutAbonnement.ACTIVE)
                        .active(true)
                        .commune(communex)
                        .role(rolex)
                        .email("acho.quebec@gmail.com")
                        .build()
        ));

// 4️⃣ Utilisateur SUPERADMIN
this.utilisateurRepository.findByEmail("acho.quebec@gmail.com")
        .orElseGet(() -> utilisateurRepository.save(
                Utilisateur.builder()
                        .commune(communex)
                        .email("acho.quebec@gmail.com")
                        .username("SUPERADMIN")
                        .password(passwordEncoder.encode("dreamcast"))
                        .role(rolex)
                        .subscription(subscriptionx)
                        .active(true)
                        .build()
        ));

		
		
		}
		

}
