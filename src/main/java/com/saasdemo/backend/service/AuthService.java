package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ActiveCode;
import com.saasdemo.backend.dto.LoginAdmin;
import com.saasdemo.backend.dto.NewPassword;
import com.saasdemo.backend.dto.ReactivedCompte;
import com.saasdemo.backend.dto.SignupRequest;
import com.saasdemo.backend.dto.SignupResponse;
import com.saasdemo.backend.entity.Commune;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Validation;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.enums.Role;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.JwtRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.security.TenantContext;
import com.saasdemo.backend.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AuthService {


  
  private final CommuneRepository communeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final ValidationService validationService;
  private final  UtilisateurRepository utilisateurRepository;
  private final UtilisateurService utilisateurService;
  private  ResponseEntity reponses;
  private Utilisateur ux = null;
  private final AuthenticationManager authenticationManager;
  private JwtRepository jwtRepository ;
 

  //Instancier 
  public AuthService(CommuneRepository communeRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil,
  ValidationService validationService,UtilisateurRepository utilisateurRepository,UtilisateurService utilisateurService, AuthenticationManager authenticationManager){
    this.communeRepository=communeRepository;
    this.passwordEncoder =passwordEncoder;
    this.jwtUtil =jwtUtil;
    this.validationService=validationService;
    this.utilisateurRepository = utilisateurRepository;
    this.utilisateurService = utilisateurService;
    this.authenticationManager = authenticationManager;
    this.jwtRepository = jwtRepository;
   
  }
    
  

//Enregistrer une Commune + son Admin
@Transactional
public SignupResponse Register( SignupRequest request){
      
    //chercher la commune
    Commune commune =  communeRepository.findByNameCommune(request.getNamecommune());
    
        if(commune==null){
          Commune communeX = this.communeRepository.save(
          Commune.builder().nameCommune(request.getNamecommune()).build());
        
          
        //implementer les données l'utilisateur
        Utilisateur utilisateur  = Utilisateur.builder()
                                  .email(request.getEmail())
                                  .username(request.getUsername())
                                  .password(passwordEncoder.encode(request.getPassword()))
                                  .role(Role.ADMIN)
                                  .commune(communeX)
                                  .active(false)
                                  .build();


        System.out.println(utilisateur);
        //sauvegarder les informations
      this.communeRepository.save(utilisateur);
        //envoyer le code pour activer le compte admin
        this.validationService.createCode(utilisateur, GenderSLC.SIGNUP);

        //creer le token après authentification
        String token = jwtUtil.generateToken(utilisateur);

       

        //renvoyer un Token null pour une commune déja inscrite
        return new SignupResponse(token);
        }else{   return new SignupResponse(" VOUS ÊTES DEJA INSCRIT");}
      
        
 
  }

   //Activation de compte Admin + Commune enregistré

   public ResponseEntity<?> activationAdmin(ActiveCode activationCompteAdmin) {
    try{Validation codex = this.validationService.getValidation(activationCompteAdmin.getCode());

    if (Instant.now().isAfter(codex.getExpirationCode())) {
      throw new RuntimeException("Le Code d'Activation a expirée");}

    Utilisateur subscriberActivatedorNot = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail()).
            orElseThrow(() -> new RuntimeException("L'Administrateur n'exite pas "));
    subscriberActivatedorNot.setActive(true);
    this.utilisateurRepository.save(subscriberActivatedorNot);
    this.jwtUtil.disableToken(subscriberActivatedorNot);
  
     this.reponses = ResponseEntity.ok().body("LE COMPTE DE "+subscriberActivatedorNot.getRole()+" "+ subscriberActivatedorNot.getUsername()+
    " EST ACTIVEE");} 
    catch(Exception e){this.reponses= ResponseEntity.badRequest().body("LE COMPTE N'A PU ÊTRE ACTIVE =>"+e.getLocalizedMessage());}
    
    return this.reponses;
   
  }


  // login + Commune
  public String loginService(LoginAdmin loginAdmin) {
  
    String retour=null;
    try{
      this.ux =  (Utilisateur) this.utilisateurService.loadUserByUsername(loginAdmin.getEmail());
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                       loginAdmin.getEmail(), loginAdmin.getPassword()));
                       System.out.println("Nom Admin :"+this.ux.getUsername());
                       TenantContext.setCurrentTenantId(ux.getCommune().getId());
                       System.out.println("TEANT ID :"+TenantContext.getCurrentTenantId());
                      this.validationService.createCode(ux,GenderSLC.LOGIN);
                      return retour=" CODE VALIDATION ENVOYE";
                      }
    catch(Exception e){retour="ADMIN NON AUTHENTIFIE=>"+e.getLocalizedMessage();}
          
    return retour;    
    }

             
 // activation login ( generation de token JWT)
  public SignupResponse activationLogin(ActiveCode activeCode){
    String tokenX=null;
    try{Validation codex = this.validationService.getValidation(activeCode.getCode());

      if (Instant.now().isAfter(codex.getExpirationCode())) {
        throw new RuntimeException("Le Code d'Activation a expirée");}
  
      Utilisateur subscriberActivatedorNot = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail()).
              orElseThrow(() -> new RuntimeException(codex.getUtilisateur().getRole()+" n'exite pas "));
      
      String  token =this.jwtUtil.generateToken(subscriberActivatedorNot);tokenX=token; 
     
    }
    
      catch(Exception e){tokenX="LE COMPTE N'A PU ÊTRE ACTIVE =>"+e.getLocalizedMessage();}
    
      
      return new SignupResponse(tokenX);
     
  
}


  // renvoi de code d'activation de compte admin de commune
   public ResponseEntity<?> renvoiCode(ReactivedCompte reactived) {
    Utilisateur alpha=null;
    RuntimeException repo = null;
      try{
          Utilisateur subscriber =   (Utilisateur) this.utilisateurService.loadUserByUsername(reactived.getEmail());
        alpha=subscriber;
        if(alpha.getActive()){repo = new RuntimeException("LE COMPTE DE L'ADMIN "+  alpha.getUsername()+" EST DEJA ACTIVEE");}
        this.validationService.createCode(alpha, null);}
      catch(Exception e){ repo = new RuntimeException("ERREUR ADMIN INCONNU");}
    return ResponseEntity.ok().body(repo.getLocalizedMessage());
     
    }


/***************************************************************************************************/

//modifier mot de passe
public void resetpassword(ReactivedCompte UpdateMotDePasse) {
  Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(UpdateMotDePasse.getEmail());
  log.info(subscriber.getUsername());
  this.validationService.createCode(subscriber, null);
}

/**************************************************************************************************/
//nouveau mot de passe
public void newpassword(NewPassword nouveauMotDePasse)  {
  Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(nouveauMotDePasse.getEmail());
  final Optional<Validation> code = Optional.ofNullable(validationService.getValidation(nouveauMotDePasse.getCode()));
  if (code.isPresent()) {
      String mdp = passwordEncoder.encode(nouveauMotDePasse.getPassword());
      subscriber.setPassword(mdp);

      this.utilisateurRepository.save(subscriber);
        }

  }


  //desactiver souscripteur USER
  public ResponseEntity<?> deletesouscripteur(ReactivedCompte emailSouscripteur) throws Exception {
    String email = emailSouscripteur.getEmail();
    Utilisateur souscris = this.utilisateurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Subscriber not found"));

    log.info("USER A DESACTIVER :"+souscris.getEmail() + " ROLE :" +souscris.getRole()+ " ACTIF :"+souscris.getActive());
    
        if(souscris.getActive() && souscris.getRole().name().equals("USER" ))
        {
          souscris.setActive(false);
          Utilisateur accord = this.utilisateurRepository.save(souscris);
          this.reponses = ResponseEntity.ok().body(accord.getRole()+" " + accord.getUsername() +" A ETE DESACTIVE");
        }
        else if(!souscris.getActive() && souscris.getRole().name().equals("USER" )){
          this.reponses =ResponseEntity.badRequest().body(souscris.getRole().name()+ " "+souscris.getUsername()+" EST DEJA DESACTIVE");
        }
        else{this.reponses =  ResponseEntity.badRequest().body(" IMPOSSIBLE DE DESACTIVER" );} 
      
     
      
      return this.reponses;

}
   
   
}

  





    
