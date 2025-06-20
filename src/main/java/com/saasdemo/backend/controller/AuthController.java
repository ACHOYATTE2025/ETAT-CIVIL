package com.saasdemo.backend.controller;

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
import com.saasdemo.backend.dto.SignupRequest;
import com.saasdemo.backend.dto.SignupResponse;
import com.saasdemo.backend.dto.UserResponse;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.UserService;
import com.saasdemo.backend.util.JwtUtil;

import jakarta.validation.Valid;




@RestController
public class AuthController {
  private final AuthService authService;
  private final UserService userService;
  private final JwtUtil jwtUtil;

  public AuthController(AuthService authService,UserService userService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.userService = userService;
    this.jwtUtil = jwtUtil;
    
  }

  //enregistrer une commune et l'Admin
  @PostMapping("/register")
  public ResponseEntity<?> register( @RequestBody @Valid SignupRequest request) {
       return this.authService.Register(request);
     
  }
  
  //activer le compte de l'Admin
 
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/accountActivation")
  public ResponseEntity<?> postMethodName(@RequestBody ActiveCodeRequest activationCompteAdmin) {
      return this.authService.activationAdmin(activationCompteAdmin);
       }

  
//Login 
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@PostMapping("/login")
public String loginAdmin(@Valid @RequestBody LoginAdminRequest loginAdmin){
   return this.authService.loginService(loginAdmin);
}


//activation login
@PreAuthorize("hasAnyRole('ADMIN','USER')")
  @PostMapping("/activationLogin")
  public SignupResponse loginActivation(@RequestBody ActiveCodeRequest activationLogin) {
      return this.authService.activationLogin(activationLogin);
       }

 //renvoi code d'activation
 //@PreAuthorize("hasAnyAuthority('ADMIN')" )
 @PostMapping(path = "/accountReactivation")
 public ResponseEntity<?> reactivationCompte(@RequestBody ReactivedCompteRequest reactived) throws Exception {
    return this.authService.renvoiCode(reactived);
 }
  
 // creation d'un user par un Admin
 @PostMapping("/userCreation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Validated @RequestBody CreateUserRequest request) {
       this.userService.createUser(request);
        return ResponseEntity.ok().body("L'USER "+request.getFullName()+" EST CREE");
    }


// obtenir l'utilisateur connceté
@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
 @GetMapping(path="/currentUser")
public UserResponse getMethodName() {
   return this.userService.getCurrentUser();
}

//modifier mot de passe
    @ResponseStatus(value = HttpStatus.FOUND)
    @PostMapping(path = "/resetPassword")
    public void ModifierMotDePasse(@RequestBody ReactivedCompteRequest UpdateMotDePasse) throws Throwable {
        this.authService.resetpassword(UpdateMotDePasse);
    }

 //nouveau mot de passe
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "/newPassword")
    public void newpassword(@RequestBody NewPasswordRequest NouveauMotDePasse) throws Throwable {
        this.authService.newpassword(NouveauMotDePasse);
    }



//deconnexion
@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
@PostMapping(path = "/end")
public ResponseEntity<?> deconex()  {
   return this.jwtUtil.deconex();
}


//desactiver un souscripteur
@PostMapping(path="/userDeactivation")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?>   deletesouscripteur(@RequestBody ReactivedCompteRequest emailSouscripteur) throws Exception {
           return this.authService.deletesouscripteur( emailSouscripteur);}

    
} 