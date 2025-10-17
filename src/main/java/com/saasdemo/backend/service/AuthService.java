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

 
  
 

  //Instancier 
  public AuthService(CommuneRepository communeRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil,
  ValidationService validationService,UtilisateurRepository utilisateurRepository,UtilisateurService utilisateurService,
   AuthenticationManager authenticationManager,SubscriptionRepository subscriptionRepository,RoleRepository roleRepository, JwtService jwtService, OperationSavingRepository opSaving){
    this.communeRepository=communeRepository;
    this.passwordEncoder =passwordEncoder;
    this.jwtUtil =jwtUtil;
    this.validationService=validationService;
    this.utilisateurRepository = utilisateurRepository;
    this.utilisateurService = utilisateurService;
    this.authenticationManager = authenticationManager;
    this.subscriptionRepository = subscriptionRepository;
    this.roleRepository = roleRepository;
    this.jwtService = jwtService;
    this.OpSaving = opSaving;
    
   
    
  
   
  }
    
  

//Enregistrer une Commune + son Admin
@Transactional
public ResponseEntity<ResponseDto> RegisterAdminService( SignupRequest request){
    ResponseEntity<ResponseDto> XXX=null;
    
    //chercher la commune
    Optional<Area> commune =   communeRepository.findByNameCommune(request.getNamecommune());
    log.info("commune :"+commune);
    if(commune.isEmpty() ){
          
      Area area= Area.builder().nameCommune(request.getNamecommune()).build();
      Area communeX = this.communeRepository.save(area);
          
    //verification email
    if(!request.getEmail().contains("@") || !request.getEmail().contains(".")){
      throw new RuntimeException("EMAIL NON VALID");
    } 

      //paramettrer role
        
        Role role =new Role();
        role.setLibele(TypeRole.ADMIN);
        this.roleRepository.save(role);

      //implementer la sousscription TRIAL
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

          
       //implementer les données l'utilisateur
        Utilisateur utilisateur  = Utilisateur.builder()
                                  .email(request.getEmail())
                                  .username(request.getUsername())
                                  .password(passwordEncoder.encode(request.getPassword()))
                                  .role(role)
                                  .commune(communeX)
                                  .active(false)
                                  .subscription(subscriptionx)
                                  .build();

        //save operation of registerAdmin in OPeration saving
        OperationsSaving savingx  = OperationsSaving.builder()
                                    .NumeroActe(subscriptionx.getNumero())
                                    .name(utilisateur.getUsername())
                                    .email(utilisateur.getEmail())
                                    .operationNature(TypeOperation.ENREGISTER_UN_ADMIN)
                                    .operationDate(Instant.now())
                                    .utilisateur(utilisateur)
                                    .build();
        this.OpSaving.save(savingx);


        log.info("Utilisateur "+ utilisateur.getId()+ " "+utilisateur.getUsername());;
        //sauvegarder les informations
        this.utilisateurRepository.save(utilisateur);
        //envoyer le code pour activer le compte admin
        this.validationService.createCode(utilisateur, GenderSLC.SIGNUP);
        XXX= ResponseEntity
        .status(HttpStatus.CREATED)
        .body(new ResponseDto(200, " VOTRE CODE VALIDATION A ETE ENVOYE"));

       
        }else{ XXX= ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ResponseDto(406, " VOUS ÊTES DEJA INSCRIT"));
          log.info("request :"+request);}
      
        return XXX;
        
 
  }

   //Activation de compte Admin + Commune enregistré

   public ResponseEntity<ResponseDto> activationAdmin(ActiveCodeRequest activationCompteAdmin) {
    try{Validation codex = this.validationService.getValidation(activationCompteAdmin.getCode());

        if (Instant.now().isAfter(codex.getExpirationCode())) {
          throw new RuntimeException("Le Code d'Activation a expirée");}

        Utilisateur subscriberActivatedorNot = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail()).
                orElseThrow(() -> new RuntimeException("L'Administrateur n'exite pas "));
        
                subscriberActivatedorNot.setActive(true);
                this.utilisateurRepository.save(subscriberActivatedorNot);
        
      
        this.reponses = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(200, "LE COMPTE DE "+subscriberActivatedorNot.getRole().getLibele()+" "+ subscriberActivatedorNot.getUsername()+
        " EST ACTIVEE"));
        this.jwtService.disableToken(subscriberActivatedorNot);
  } 
    catch(Exception e){this.reponses= ResponseEntity 
      .status(HttpStatus.BAD_REQUEST)
      .body(new ResponseDto(401, "LE COMPTE N'A PU ÊTRE ACTIVE -> "+e.getLocalizedMessage()));}
    
    
    return this.reponses;
   
  }


  // login + Commune
  public ResponseEntity<ResponseDto> loginService(LoginAdminRequest loginAdmin) {
  
    ResponseEntity<ResponseDto> retour=null;
    try{
      this.ux =  (Utilisateur) this.utilisateurService.loadUserByUsername(loginAdmin.getEmail());
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                       loginAdmin.getEmail(), loginAdmin.getPassword()));
                       System.out.println("Nom Admin :"+this.ux.getUsername());
                       TenantContext.setCurrentTenantId(ux.getCommune().getId());
                       System.out.println("TEANT ID :"+TenantContext.getCurrentTenantId());
                      this.validationService.createCode(ux,GenderSLC.LOGIN);
                      ux.setConnected(true);
                      this.utilisateurRepository.save(ux);
                      return (ResponseEntity<ResponseDto>) (retour =ResponseEntity
                      .status(HttpStatus.OK)
                      .body(new ResponseDto(200, " CODE VALIDATION ENVOYE")));
                      
                      }
    catch(Exception e){
      retour = ResponseEntity
                      .status(HttpStatus.NOT_ACCEPTABLE)
                      .body(new ResponseDto(406, e.getLocalizedMessage()));}
          
       return  retour;
    }

  
 // activation login ( generation de token JWT)
  public SignupResponse activationLogin(ActiveCodeRequest activeCode){
        SignupResponse tokenx = new SignupResponse("", "");
    try{Validation codex = this.validationService.getValidation(activeCode.getCode());
          System.out.println("codex :"+codex);
        
          if (Instant.now().isAfter(codex.getExpirationCode())) {
            throw new RuntimeException("Le Code d'Activation a expirée");}
      
          Utilisateur subscriberActivated = this.utilisateurRepository.findByEmail(codex.getUtilisateur().getEmail()).
                  orElseThrow(() -> new RuntimeException(codex.getUtilisateur().getRole()+" n'exite pas "));
          
          tokenx =this.jwtService.generateAndSaveToken(subscriberActivated);
          
          //make user connected
          log.info(subscriberActivated.getId().toString()+" "+subscriberActivated.getUsername()+" connected" );
          
          subscriberActivated.setConnected(true);
          this.utilisateurRepository.save(subscriberActivated);
          
              }
    
    catch(Exception e){ tokenx= null;}
    
      
      return tokenx;
     
  
}


  // renvoi de code d'activation de compte admin de commune
   public ResponseEntity<ResponseDto> renvoiCode(ReactivedCompteRequest reactived) {
    Utilisateur alpha=null;
    ResponseEntity<ResponseDto> respond = null;
    
      
        Utilisateur subscriber =   (Utilisateur) this.utilisateurService.loadUserByUsername(reactived.getEmail());
        
        alpha=subscriber;
        if(alpha.getActive()){
          new RuntimeException("LE COMPTE DE L'ADMIN "+  alpha.getUsername()+" EST DEJA ACTIVEE");
          return
            respond = ResponseEntity
            .status(HttpStatus.FOUND)
            .body(new ResponseDto(200, "LE COMPTE DE L'ADMIN "+  alpha.getUsername()+" EST DEJA ACTIVEE"));
        }

        else if(!subscriber.getActive()){
          this.validationService.createCode(subscriber, GenderSLC.SIGNUP);
          return 
          respond = ResponseEntity
          .status(HttpStatus.FOUND)
          .body(new ResponseDto(200, "CODE D'ACTIVATION ENVOYE"));}
        else{
          return 
          respond=ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ResponseDto(401, "ADMIN INCONNU"));}
        
      }
        
          
    
  

/***************************************************************************************************/

//modifier mot de passe
public void resetpassword(ReactivedCompteRequest UpdateMotDePasse) {
  Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(UpdateMotDePasse.getEmail());
  log.info(subscriber.getUsername());
  this.validationService.createCode(subscriber, null);
}

/**************************************************************************************************/
//nouveau mot de passe
public void newpassword(NewPasswordRequest nouveauMotDePasse)  {
  Utilisateur subscriber = (Utilisateur) this.utilisateurService.loadUserByUsername(nouveauMotDePasse.getEmail());
  final Optional<Validation> code = Optional.ofNullable(validationService.getValidation(nouveauMotDePasse.getCode()));
  if (code.isPresent()) {
      String mdp = passwordEncoder.encode(nouveauMotDePasse.getPassword());
      subscriber.setPassword(mdp);

      this.utilisateurRepository.save(subscriber);
        }

  }

 //desactiver souscripteur USER
  public ResponseEntity<ResponseDto> desactivatesubscriberService(ReactivedCompteRequest emailSouscripteur) throws Exception {
    String email = emailSouscripteur.getEmail();
    Utilisateur souscris = this.utilisateurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Subscriber not found"));

    log.info("USER A DESACTIVER :"+souscris.getEmail() + " ROLE :" +souscris.getRole()+ " ACTIF :"+souscris.getActive());

        if(souscris.getActive() && souscris.getRole().getLibele().equals("USER" ))
        {
          souscris.setActive(false);
          Utilisateur accord = this.utilisateurRepository.save(souscris);
          this.reponses = ResponseEntity
                          .status(HttpStatus.OK)
                          .body(new ResponseDto( 200, accord.getRole().getLibele()+" " + accord.getUsername() +" A ETE DESACTIVE"));
        }
        else if(!souscris.getActive() && souscris.getRole().equals("USER" )){
          this.reponses =ResponseEntity.badRequest().body(souscris.getRole().getLibele()+ " "+souscris.getUsername()+" EST DEJA DESACTIVE");
        }
        else{this.reponses =  ResponseEntity
          .status(HttpStatus.BAD_GATEWAY)
          .body(new ResponseDto( 400," IMPOSSIBLE DE DESACTIVER" ));}
        
        
        return this.reponses;}

}





    
