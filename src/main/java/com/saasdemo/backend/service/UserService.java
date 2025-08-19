package com.saasdemo.backend.service;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.CreateUserRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.JwtRepository;
import com.saasdemo.backend.repository.RoleRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
;

@Service
public class UserService {

  private final UtilisateurRepository utilisateurRepository ;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final RoleRepository roleRepository;

    public UserService(JwtRepository jwtRepository, UtilisateurRepository utilisateurRepository, CommuneRepository communeRepository, PasswordEncoder passwordEncoder, NotificationService notificationService, ValidationService validationService, RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.roleRepository = roleRepository;
      
    }



    //creation des users par un Admin d'une commune
    public ResponseEntity<ResponseDto> createUser(CreateUserRequest request) {
        ResponseEntity<ResponseDto> respondx = null;
        // Récupère l'organisation de l'admin connecté
        try {Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Area commune = admin.getCommune();

       if(!request.getEmail().contains("@") || !request.getEmail().contains(".") ){
        throw new RuntimeException("Your Email must be Correct!!!!!");
       }
        //mise en place du role
        Role roleUser = roleRepository.findByLibele(admin.getRole().getLibele())
                .orElseThrow(() -> new RuntimeException("ROLE USER NOT FOUND"));

        roleUser.setLibele(TypeRole.USER);
        this.roleRepository.save(roleUser);

        // Créer un nouvel utilisateur dans la même organisation
        Utilisateur newUser = Utilisateur.builder()
                .username(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleUser) // Par défaut, l'utilisateur créé a le rôle USER
                .active(true)
                .commune(commune)
                .build();
       
        this.validationService.createCode(newUser,GenderSLC.USER_CREATION);
        this.utilisateurRepository.save(newUser);
        respondx = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(200,request.getFullName()+" CREATED SUCCESSFULL"));
      }
    catch(Exception e){return  
        respondx = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(401,"USER CREATION FAILED"));
      }

    return respondx;
    }


// connaitre l'utilisateur connecté
    public List<Utilisateur> getAllCurrentUserConnected() {
        List<TypeRole> roles = List.of(TypeRole.ADMIN, TypeRole.USER);
        List<Utilisateur> listOfConnected = this.utilisateurRepository.findAll();
      return listOfConnected;
}
}