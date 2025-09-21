package com.saasdemo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.ActiveCodeRequest;
import com.saasdemo.backend.dto.CreateUserRequest;
import com.saasdemo.backend.dto.LoginAdminRequest;
import com.saasdemo.backend.dto.NewPasswordRequest;
import com.saasdemo.backend.dto.ReactivedCompteRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.dto.SignupRequest;
import com.saasdemo.backend.dto.SignupResponse;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.JwtService;
import com.saasdemo.backend.service.PdfService;
import com.saasdemo.backend.service.SubscriptionService;
import com.saasdemo.backend.service.UtilisateurService;
import com.saasdemo.backend.util.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthController handles authentication and user management REST endpoints for the ETAT CIVIL application.
 */
@RestController
@Slf4j
@Tag(
  name = "AUTHENTIFICATION REST Api for ETAT CIVIL",
  description="AUTHENTIFICATION REST Api in ETAT CIVIL APP to CREATE, READ, UPDATE, DELETE details"
)
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final UtilisateurService utilisateurService;
    private final JwtUtil jwtUtil;
    private final SubscriptionService subscriptionService;
    private final JwtService jService;
    private final PdfService pdfService;

    public AuthController(AuthService authService, UtilisateurService utilisateurService,
                        JwtUtil jwtUtil, SubscriptionService subscriptionService,
                        JwtService jwtService, JwtService jService, PdfService pdfService) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
        this.jwtUtil = jwtUtil;
        this.subscriptionService = subscriptionService;
        this.jwtService = jwtService;
        this.jService = jService;
        this.pdfService = pdfService;
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<ResponseDto> registerAdmin(@RequestBody @Valid SignupRequest request) throws Exception {
        log.info("Registering new admin with email: {}", request.getEmail());
        return authService.RegisterAdminService(request);
    }

    public ResponseEntity<ResponseDto> activationAdmin(@RequestBody ActiveCodeRequest activationCompteAdmin) {
        log.info("Activating admin account with code: {}", activationCompteAdmin.getCode());
        return (ResponseEntity<ResponseDto>) this.authService.activationAdmin(activationCompteAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@Valid @RequestBody LoginAdminRequest loginAdmin){
        log.info("Login attempt for username: {}", loginAdmin);
        return (ResponseEntity<ResponseDto>) this.authService.loginService(loginAdmin);
    }

    @PostMapping("/loginvalidation")
    public SignupResponse loginActivation(@RequestBody ActiveCodeRequest activationLogin) {
        log.info("Validating login with code: {}", activationLogin.getCode());
        return this.authService.activationLogin(activationLogin);
    }

    @PostMapping("/refreshtoken")
    public SignupResponse refreshToken(@RequestBody ActiveCodeRequest refreshTokenRequest) {
        log.info("Refreshing token with RefreshToken: {}", refreshTokenRequest.getCode());
        return this.jwtService.refreshtoken(refreshTokenRequest);
    }

    @PostMapping(path = "/accountReactivation")
    public ResponseEntity<?> reactivationCompte(@RequestBody ReactivedCompteRequest reactived) throws Exception {
        log.info("Reactivating account for email: {}", reactived.getEmail());
        return this.authService.renvoiCode(reactived);
    }

    @PostMapping("/userCreation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createUser(@Validated @RequestBody CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());
        return this.utilisateurService.createUser(request);
    }

    @GetMapping(path="/allusersConnected")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<Utilisateur> getAllUsersConnectedController() {
        log.info("Fetching all currently connected users");
        return this.utilisateurService.getAllCurrentUserConnected();
    }

    @PostMapping(path = "/resetPassword")
    public void ModifierMotDePasse(@RequestBody ReactivedCompteRequest UpdateMotDePasse) throws Throwable {
        log.info("Password reset requested for email: {}", UpdateMotDePasse.getEmail());
        this.authService.resetpassword(UpdateMotDePasse);
    }

    @PostMapping(path = "/newPassword")
    public void newpassword(@RequestBody NewPasswordRequest NouveauMotDePasse) throws Throwable {
        log.info("Setting new password for email: {}", NouveauMotDePasse.getEmail());
        this.authService.newpassword(NouveauMotDePasse);
    }

    @PostMapping(path = "/deconnexion")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
    public ResponseEntity<?> deconex()  {
        log.info("Logout request received");
        return this.jwtService.deconex();
    }

    @PostMapping(path="/userDeactivation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> desactivateSubscriber(@RequestBody ReactivedCompteRequest emailSouscripteur) throws Exception {
        log.info("Deactivating subscriber with email: {}", emailSouscripteur.getEmail());
        return this.authService.desactivatesubscriberService(emailSouscripteur);
    }
}
