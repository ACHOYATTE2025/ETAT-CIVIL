package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.CreateUserRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.OperationsSaving;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.enums.TypeOperation;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.OperationSavingRepository;
import com.saasdemo.backend.repository.RoleRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilisateurService implements UserDetailsService {

    // Logger for debugging and information
    private static final Logger logger = Logger.getLogger(UtilisateurService.class.getName());

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final RoleRepository roleRepository;
    private final OperationSavingRepository OpSaving;

    /**
     * Loads a user by their email for authentication.
     * @param email the user email
     * @return UserDetails if found, else throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info("[AUTH] Attempting to load user by email: " + email);
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("[AUTH] User not found: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }

    /**
     * Admin creates a new user within his commune (organization).
     * Validates email, assigns USER role, saves user, and logs operation.
     * @param request details for creating the user
     * @return ResponseEntity with creation status
     */
    public ResponseEntity<ResponseDto> createUser(CreateUserRequest request) {
        ResponseEntity<ResponseDto> respondx;
        try {
            // Get the currently authenticated admin
            Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Area commune = admin.getCommune();
            logger.info("[CREATE USER] Admin '" + admin.getUsername() + "' (Commune: " + commune.getNameCommune() + 
                        ") attempts to create user with email: " + request.getEmail());

            // Validate email format
            if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
                logger.warning("[CREATE USER] Invalid email format: " + request.getEmail());
                throw new RuntimeException("Your Email must be Correct!!!!!");
            }

            // Create and save new USER role
            logger.info("[CREATE USER] Assigning default USER role to: " + request.getEmail());
            Role roleUser = new Role();
            roleUser.setLibele(TypeRole.USER);
            this.roleRepository.save(roleUser);
            logger.info("[CREATE USER] USER role saved successfully for: " + request.getEmail());

            // Build new user and encode password
            logger.info("[CREATE USER] Encoding password and building new user entity...");
            Utilisateur newUser = Utilisateur.builder()
                    .username(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(roleUser) // Default role is USER
                    .active(true)
                    .commune(commune)
                    .build();

            // Generate validation code and save user
            logger.info("[CREATE USER] Generating validation code for: " + request.getEmail());
            this.validationService.createCode(newUser, GenderSLC.USER_CREATION);

            logger.info("[CREATE USER] Saving new user: " + newUser.getEmail());
            this.utilisateurRepository.save(newUser);
            logger.info("[CREATE USER] User '" + request.getFullName() + "' created successfully");

            // Log the operation of user registration
            logger.info("[AUDIT] Logging creation operation for admin: " + admin.getUsername());
            OperationsSaving savingx = OperationsSaving.builder()
                    .name(admin.getUsername())
                    .email(admin.getEmail())
                    .operationNature(TypeOperation.ENREGISTRER_UN_USER)
                    .operationDate(Instant.now())
                    .utilisateur(admin)
                    .build();
            this.OpSaving.save(savingx);
            logger.info("[AUDIT] Operation logged successfully");

            respondx = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(200, request.getFullName() + " CREATED SUCCESSFULLY"));
        } catch (Exception e) {
            logger.severe("[CREATE USER] User creation failed for email: " 
                          + request.getEmail() + " | Reason: " + e.getMessage());
            respondx = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(401, "USER CREATION FAILED"));
        }

        return respondx;
    }

    /**
     * Retrieves all currently connected users (ADMIN and USER roles).
     * @return List of connected users
     */
    public List<Utilisateur> getAllCurrentUserConnected() {
        List<TypeRole> roles = List.of(TypeRole.ADMIN, TypeRole.USER);
        logger.info("[CONNECTED USERS] Retrieving all connected users with roles: " + roles);
        List<Utilisateur> listOfConnected = this.utilisateurRepository.findAll();
        logger.info("[CONNECTED USERS] Found " + listOfConnected.size() + " connected users.");
        return listOfConnected;
    }
}
