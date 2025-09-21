package com.saasdemo.backend.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ActiveCodeRequest;
import com.saasdemo.backend.dto.LoginAdminRequest;
import com.saasdemo.backend.dto.NewPasswordRequest;
import com.saasdemo.backend.dto.ReactivedCompteRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.dto.SignupRequest;
import com.saasdemo.backend.dto.SignupResponse;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.OperationsSaving;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Validation;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.enums.TypeOperation;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.OperationSavingRepository;
import com.saasdemo.backend.repository.RoleRepository;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.security.TenantContext;
import com.saasdemo.backend.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

  private final JwtService jwtService;
  private final CommuneRepository communeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final ValidationService validationService;
  private final  UtilisateurRepository utilisateurRepository;
  private final UtilisateurService utilisateurService;
  private  ResponseEntity reponses;
  private Utilisateur ux = null;
  private final AuthenticationManager authenticationManager;
  private final SubscriptionRepository subscriptionRepository;
  private final RoleRepository roleRepository;
  private final OperationSavingRepository OpSaving;

  // Instantiate the service with all required dependencies
  public AuthService(CommuneRepository communeRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
      ValidationService validationService, UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService,
      AuthenticationManager authenticationManager, SubscriptionRepository subscriptionRepository, RoleRepository roleRepository,
      JwtService jwtService, OperationSavingRepository opSaving) {
    this.communeRepository = communeRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.validationService = validationService;
    this.utilisateurRepository = utilisateurRepository;
    this.utilisateurService = utilisateurService;
    this.authenticationManager = authenticationManager;
    this.subscriptionRepository = subscriptionRepository;
    this.roleRepository = roleRepository;
    this.jwtService = jwtService;
    this.OpSaving = opSaving;
  }

  /**
   * Register a new Commune and its Admin
   */
  @Transactional
  public ResponseEntity<ResponseDto> RegisterAdminService(SignupRequest request) {
    log.info("Received RegisterAdminService request: {}", request);
    ResponseEntity<ResponseDto> XXX = null;

    // Search for the commune
    Optional<Area> commune = communeRepository.findByNameCommune(request.getNamecommune());
    log.info("Looking for commune: {}", request.getNamecommune());

    if (commune.isEmpty()) {
      log.info("Commune not found, creating new commune");
      // Create the new commune
      Area area = Area.builder().nameCommune(request.getNamecommune()).build();
      Area communeX = this.communeRepository.save(area);

      log.info("Commune created: {}", communeX);

      // Email validation
      if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
        log.error("Invalid email provided: {}", request.getEmail());
        throw new RuntimeException("EMAIL NON VALID");
      }

      // Set up the admin role
      Role role = new Role();
      role.setLibele(TypeRole.ADMIN);
      this.roleRepository.save(role);
      log.info("Admin role created: {}", role);

      // Implement a TRIAL subscription for the admin
      Subscription subscriptionx = Subscription.builder()
          .numero(UUID.randomUUID().toString())
          .usersName(request.getUsername())
          .amount(0)
          .created(LocalDateTime.now())
          .endDate(LocalDateTime.now().plusWeeks(1))
          .status(StatutAbonnement.TRIAL)
          .active(true)
          .commune(area)
          .role(role)
          .email(request.getEmail())
          .build();

      this.subscriptionRepository.save(subscriptionx);
      log.info("Trial subscription created: {}", subscriptionx);

      // Create the user data for the admin
      Utilisateur utilisateur = Utilisateur.builder()
          .email(request.getEmail())
          .username(request.getUsername())
          .password(passwordEncoder.encode(request.getPassword()))
          .role(role)
          .commune(communeX)
          .active(false)
          .subscription(subscriptionx)
          .build();

      // Save the registerAdmin operation in OperationsSaving
      OperationsSaving savingx = OperationsSaving.builder()
          .NumeroActe(subscriptionx.getNumero())
          .name(utilisateur.getUsername())
          .email(utilisateur.getEmail())
          .operationNature(TypeOperation.ENREGISTER_UN_ADMIN)
          .operationDate(Instant.now())
          .utilisateur(utilisateur)
          .build();
      this.OpSaving.save(savingx);

      log.info("Saving operation for new admin: {}", savingx);

      log.info("Utilisateur {} {} created", utilisateur.getId(), utilisateur.getUsername());
      // Save user info
      this.utilisateurRepository.save(utilisateur);

      // Send activation code for admin account
      this.validationService.createCode(utilisateur, GenderSLC.SIGNUP);
      log.info("Activation code sent for user: {}", utilisateur.getEmail());

      XXX = ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new ResponseDto(200, "YOUR VALIDATION CODE HAS BEEN SENT"));

    } else {
      log.warn("User already registered for commune: {}", request.getNamecommune());
      XXX = ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ResponseDto(406, "YOU ARE ALREADY REGISTERED"));
      log.info("Request details: {}", request);
    }

    return XXX;
  }

  /**
   * Activate Admin account and registered Commune
   */
  public ResponseEntity<ResponseDto> activationAdmin(ActiveCodeRequest activationCompteAdmin) {
    log.info("Received activationAdmin request: {}", activationCompteAdmin);
    try {
      Validation codex = this.validationService.getValidation(activationCompteAdmin.getCode());
      log.info("Found validation code: {}", codex);

      if (Instant.now().isAfter(codex.getExpirationCode())) {
        log.error("Activation code expired for user: {}", codex.getUtilisateur().getEmail());
        throw new RuntimeException("The Activation Code has expired");
      }

      Utilisateur subscriberActivatedorNot = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail())
          .orElseThrow(() -> new RuntimeException("Administrator does not exist"));

      subscriberActivatedorNot.setActive(true);
      this.utilisateurRepository.save(subscriberActivatedorNot);

      log.info("Account activated for: {}", subscriberActivatedorNot.getEmail());

      this.reponses = ResponseEntity
          .status(HttpStatus.OK)
          .body(new ResponseDto(200, "THE ACCOUNT OF " + subscriberActivatedorNot.getRole().getLibele() + " " +
              subscriberActivatedorNot.getUsername() + " IS ACTIVATED"));
      this.jwtService.disableToken(subscriberActivatedorNot);
      log.info("Token disabled for user: {}", subscriberActivatedorNot.getEmail());
    } catch (Exception e) {
      log.error("Failed to activate account: {}", e.getLocalizedMessage());
      this.reponses = ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ResponseDto(401, "THE ACCOUNT COULD NOT BE ACTIVATED -> " + e.getLocalizedMessage()));
    }

    return this.reponses;
  }

  /**
   * Admin login + Commune
   */
  public ResponseEntity<ResponseDto> loginService(LoginAdminRequest loginAdmin) {
    log.info("Received loginService request: {}", loginAdmin);
    ResponseEntity<ResponseDto> retour = null;
    try {
      this.ux = (Utilisateur) this.utilisateurService.loadUserByUsername(loginAdmin.getEmail());
      log.info("Loaded user: {}", this.ux.getUsername());
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          loginAdmin.getEmail(), loginAdmin.getPassword()));
      log.info("Authentication successful for: {}", loginAdmin.getEmail());
      log.info("Admin Name: {}", this.ux.getUsername());
      TenantContext.setCurrentTenantId(ux.getCommune().getId());
      log.info("TENANT ID: {}", TenantContext.getCurrentTenantId());
      this.validationService.createCode(ux, GenderSLC.LOGIN);
      log.info("Login validation code sent for: {}", ux.getEmail());
      ux.setConnected(true);
      this.utilisateurRepository.save(ux);
      retour = ResponseEntity
          .status(HttpStatus.OK)
          .body(new ResponseDto(200, "VALIDATION CODE SENT"));
    } catch (Exception e) {
      log.error("Login failed: {}", e.getLocalizedMessage());
      retour = ResponseEntity
          .status(HttpStatus.NOT_ACCEPTABLE)
          .body(new ResponseDto(406, e.getLocalizedMessage()));
    }
    return retour;
  }

  /**
   * Activation of login (JWT token generation)
   */
  public SignupResponse activationLogin(ActiveCodeRequest activeCode) {
    log.info("Received activationLogin request: {}", activeCode);
    SignupResponse tokenx = new SignupResponse("", "");
    try {
      Validation codex = this.validationService.getValidation(activeCode.getCode());
      log.info("Validation code found: {}", codex);

      if (Instant.now().isAfter(codex.getExpirationCode())) {
        log.error("Activation code expired for login: {}", codex.getUtilisateur().getEmail());
        throw new RuntimeException("The Activation Code has expired");
      }

      Utilisateur subscriberActivated = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail())
          .orElseThrow(() -> new RuntimeException(codex.getUtilisateur().getRole() + " does not exist"));

      tokenx = this.jwtService.generateAndSaveToken(subscriberActivated);

      log.info("Token generated and saved for: {}", subscriberActivated.getEmail());

      // Mark user as connected
      log.info("User {} {} connected", subscriberActivated.getId(), subscriberActivated.getUsername());

      subscriberActivated.setConnected(true);
      this.utilisateurRepository.save(subscriberActivated);

    } catch (Exception e) {
      log.error("Activation login failed: {}", e.getLocalizedMessage());
      tokenx = null;
    }
    return tokenx;
  }

  /**
   * Resend activation code for admin commune account
   */
  public ResponseEntity<ResponseDto> renvoiCode(ReactivedCompteRequest reactived) {
    log.info("Received renvoiCode request: {}", reactived);
    Utilisateur alpha = null;
    ResponseEntity<ResponseDto> respond = null;

    Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(reactived.getEmail());

    alpha = subscriber;
    if (alpha.getActive()) {
      log.warn("Admin account already activated: {}", alpha.getUsername());
      new RuntimeException("THE ACCOUNT OF ADMIN " + alpha.getUsername() + " IS ALREADY ACTIVATED");
      respond = ResponseEntity
          .status(HttpStatus.FOUND)
          .body(new ResponseDto(200, "THE ACCOUNT OF ADMIN " + alpha.getUsername() + " IS ALREADY ACTIVATED"));
      return respond;
    } else if (!subscriber.getActive()) {
      log.info("Resending activation code to: {}", subscriber.getEmail());
      this.validationService.createCode(subscriber, GenderSLC.SIGNUP);
      respond = ResponseEntity
          .status(HttpStatus.FOUND)
          .body(new ResponseDto(200, "ACTIVATION CODE SENT"));
      return respond;
    } else {
      log.error("Unknown admin: {}", reactived.getEmail());
      respond = ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ResponseDto(401, "UNKNOWN ADMIN"));
      return respond;
    }
  }

  /**************************************************************************************************/

  /**
   * Reset password (send code)
   */
  public void resetpassword(ReactivedCompteRequest UpdateMotDePasse) {
    log.info("Received resetpassword request: {}", UpdateMotDePasse);
    Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(UpdateMotDePasse.getEmail());
    log.info("Resetting password for user: {}", subscriber.getUsername());
    this.validationService.createCode(subscriber, null);
    log.info("Password reset code sent to: {}", subscriber.getEmail());
  }

  /**************************************************************************************************/
  /**
   * Set new password
   */
  public void newpassword(NewPasswordRequest nouveauMotDePasse) {
    log.info("Received newpassword request: {}", nouveauMotDePasse);
    Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(nouveauMotDePasse.getEmail());
    final Optional<Validation> code = Optional.ofNullable(validationService.getValidation(nouveauMotDePasse.getCode()));
    if (code.isPresent()) {
      log.info("Validation code present for password update");
      String mdp = passwordEncoder.encode(nouveauMotDePasse.getPassword());
      subscriber.setPassword(mdp);

      this.utilisateurRepository.save(subscriber);
      log.info("Password updated for user: {}", subscriber.getEmail());
    } else {
      log.warn("Validation code not found for password update: {}", nouveauMotDePasse.getEmail());
    }
  }

  /**
   * Deactivate subscriber USER
   */
  public ResponseEntity<ResponseDto> desactivatesubscriberService(ReactivedCompteRequest emailSouscripteur) throws Exception {
    log.info("Received desactivatesubscriberService request: {}", emailSouscripteur);
    String email = emailSouscripteur.getEmail();
    Utilisateur souscris = this.utilisateurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Subscriber not found"));

    log.info("USER TO DEACTIVATE: {} ROLE: {} ACTIVE: {}", souscris.getEmail(), souscris.getRole(), souscris.getActive());

    if (souscris.getActive() && souscris.getRole().getLibele().equals("USER")) {
      souscris.setActive(false);
      Utilisateur accord = this.utilisateurRepository.save(souscris);
      log.info("User {} deactivated", accord.getEmail());
      this.reponses = ResponseEntity
          .status(HttpStatus.OK)
          .body(new ResponseDto(200, accord.getRole().getLibele() + " " + accord.getUsername() + " HAS BEEN DEACTIVATED"));
    } else if (!souscris.getActive() && souscris.getRole().equals("USER")) {
      log.warn("User already deactivated: {}", souscris.getEmail());
      this.reponses = ResponseEntity.badRequest().body(souscris.getRole().getLibele() + " " + souscris.getUsername() + " IS ALREADY DEACTIVATED");
    } else {
      log.error("Impossible to deactivate user: {}", souscris.getEmail());
      this.reponses = ResponseEntity
          .status(HttpStatus.BAD_GATEWAY)
          .body(new ResponseDto(400, "IMPOSSIBLE TO DEACTIVATE"));
    }

    return this.reponses;
  }
}
