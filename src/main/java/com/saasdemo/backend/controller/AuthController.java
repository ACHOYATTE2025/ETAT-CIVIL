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
import com.saasdemo.backend.dto.ErrorResponseDto;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(
  name = "AUTHENTIFICATION   REST Api for ETAT CIVIL",
  description="AUTHENTIFICATION REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
@RestController
public class AuthController {

private final JwtService jwtService;
private final AuthService authService;
private final UtilisateurService utilisateurService;
private final JwtUtil jwtUtil;
private final SubscriptionService subscriptionService;
private final JwtService jService;
private final PdfService pdfService;

  public AuthController(AuthService authService,UtilisateurService utilisateurService, 
  JwtUtil jwtUtil,SubscriptionService subscriptionService, JwtService jwtService, JwtService jService, PdfService pdfService) {
    this.authService = authService;
    this.utilisateurService = utilisateurService;
    this.jwtUtil = jwtUtil;
    this.subscriptionService = subscriptionService;
    this.jwtService = jwtService;
    this.jService = jService;
    this.pdfService = pdfService;
    
    
  }

  //Inscrire une commune et l'Admin
  
//create
  @Operation(
    summary="REST API to create new Admin into APP ETAT CIVIL",
    description = "REST API to create new Account  inside ETAT CIVIL App "
  )

  @ApiResponse(
    responseCode="201",
    description = "HTTP Status CREATED"
  )
  @PostMapping("/registerAdmin")
  public ResponseEntity<ResponseDto> registerAdmin( @RequestBody @Valid SignupRequest request)throws Exception {
        return (ResponseEntity<ResponseDto>) this.authService.RegisterAdminService(request);
       
  }
  
  //activer le compte de l'Admin
 
  @Operation(
    summary="REST API to activate new Admin into APP ETAT CIVIL",
    description = "REST API to activate new Account Admin  inside ETAT CIVIL APP"
  )

  @ApiResponses({
    @ApiResponse(
        responseCode="200",
        description = "HTTP Status DONE",
        content = @Content(
            schema = @Schema(implementation = ResponseDto.class)) ),
    
    @ApiResponse(   
        responseCode = "401",
        description = "Activation  failed!!!",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
    )
    }
  )
  @PostMapping("/accountActivation")
  public ResponseEntity<ResponseDto> ActiveAdminAccount(@RequestBody ActiveCodeRequest activationCompteAdmin) {
      return (ResponseEntity<ResponseDto>) this.authService.activationAdmin(activationCompteAdmin);
      

       }

  
//Login 
 @Operation(
    summary="REST API to login  Admin or User into APP ETAT CIVIL",
    description = "REST API to login  Admin or  User inside ETAT CIVIL APP"
  )

  @ApiResponses({
    @ApiResponse(
        responseCode="200",
        description = "HTTP Status DONE",
        content = @Content(
            schema = @Schema(implementation = ResponseDto.class)) ),
    
    @ApiResponse(   

        description = "Login  failed!!!",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
    )
    }
  )
@PostMapping("/login")
public ResponseEntity<ResponseDto> login(@Valid @RequestBody LoginAdminRequest loginAdmin){
   return (ResponseEntity<ResponseDto>) this.authService.loginService(loginAdmin);
}


//activation login
@Operation(
    summary="REST API to activate login into APP ETAT CIVIL",
    description = "REST API to activate login inside ETAT CIVIL APP"
  )

  @ApiResponses({
    @ApiResponse(
        responseCode="200",
        description = "HTTP Status DONE",
        content = @Content(
            schema = @Schema(implementation = ResponseDto.class)) ),
    
    @ApiResponse(   
        description = "Login Activation failed!!!",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
    )
    }
  )
//@PreAuthorize("hasAnyRole('ADMIN','USER')")
@PostMapping("/loginvalidation")
public SignupResponse loginActivation(@RequestBody ActiveCodeRequest activationLogin) {
      return this.authService.activationLogin(activationLogin);
       }



//refresh Token
@Operation(
    summary="REST API to make refreshtoken into APP ETAT CIVIL",
    description = "REST API to make refreshtoken  inside ETAT CIVIL APP"
  )
//@PreAuthorize("hasAnyRole('USER')")
  @PostMapping("/refreshtoken")
  public  SignupResponse refreshToken(@RequestBody ActiveCodeRequest refreshTokenRequest) {
       return this.jwtService.refreshtoken(refreshTokenRequest);
       }


 //renvoi code d'activation
 @Operation(
    summary="REST API to reactivate admin account into APP ETAT CIVIL",
    description = "REST API to reactivate admin account inside ETAT CIVIL APP"
  )
  @ApiResponses({
    @ApiResponse(
        responseCode="200",
        description = "HTTP Status DONE",
        content = @Content(
            schema = @Schema(implementation = ResponseDto.class)) ),
    
    @ApiResponse(   
        description = "Recativation failed!!!",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
    )
    }
  )
 //@PreAuthorize("hasAnyAuthority('ADMIN')" )
 @PostMapping(path = "/accountReactivation")
 public ResponseEntity<?> reactivationCompte(@RequestBody ReactivedCompteRequest reactived) throws Exception {
    return this.authService.renvoiCode(reactived);
 }


  
 // creation d'un user par un Admin
  @Operation(
    summary="REST API to create USER ACCOUNT into APP ETAT CIVIL",
    description = "REST API to create USER ACCOUNT inside ETAT CIVIL APP"
  )
 @ApiResponses({
    @ApiResponse(
        responseCode="200",
        description = "HTTP Status DONE",
        content = @Content(
            schema = @Schema(implementation = ResponseDto.class)) ),
    
    @ApiResponse(   
        description = "User Creation failed!!!",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
    )
    }
  )
    @PostMapping("/userCreation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createUser(@Validated @RequestBody CreateUserRequest request) {
       return this.utilisateurService.createUser(request);
       
    }


// obtenir l'utilisateur connceté
@PreAuthorize("hasRole('SUPERADMIN')")
@GetMapping(path="/allusersConnected")
public List<Utilisateur> getAllUsersConnectedController() {
  return this.utilisateurService.getAllCurrentUserConnected();
}

//modifier mot de passe
@Operation(
    summary="REST API to reset password USER into APP ETAT CIVIL",
    description = "REST API to reset password USER inside ETAT CIVIL APP"
  )
@ResponseStatus(value = HttpStatus.FOUND)
@PostMapping(path = "/resetPassword")
    public void ModifierMotDePasse(@RequestBody ReactivedCompteRequest UpdateMotDePasse) throws Throwable {
        this.authService.resetpassword(UpdateMotDePasse);
    }

 //nouveau mot de passe
 @Operation(
    summary="REST API to make new password USER into APP ETAT CIVIL",
    description = "REST API to make new  password USER inside ETAT CIVIL APP"
  )
  @ResponseStatus(value = HttpStatus.CREATED)
  @PostMapping(path = "/newPassword")
    public void newpassword(@RequestBody NewPasswordRequest NouveauMotDePasse) throws Throwable {
        this.authService.newpassword(NouveauMotDePasse);
    }



//deconnexion
 @Operation(
    summary="REST API to deconnect USER,ADMIN into APP ETAT CIVIL",
    description = "REST API to deconnect USER,ADMIN inside ETAT CIVIL APP"
  )
@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
@PostMapping(path = "/deconnexion")
public ResponseEntity<?> deconex()  {
   return this.jwtService.deconex();
}


//desactiver un souscripteur
 @Operation(
    summary="REST API to desactivate USER into APP ETAT CIVIL",
    description = "REST API to desactivate USER inside ETAT CIVIL APP"
  )
@PostMapping(path="/userDeactivation")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ResponseDto>   desactivateSubscriber(@RequestBody ReactivedCompteRequest emailSouscripteur) throws Exception {
           return this.authService.desactivatesubscriberService( emailSouscripteur);}

    


        
}