package com.saasdemo.backend.service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.CreateUserRequest;
import com.saasdemo.backend.dto.UserResponse;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.JwtRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
;

@Service
public class UserService {

  private final UtilisateurRepository utilisateurRepository ;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    public UserService(JwtRepository jwtRepository, UtilisateurRepository utilisateurRepository, CommuneRepository communeRepository, PasswordEncoder passwordEncoder, NotificationService notificationService, ValidationService validationService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
      
    }



    //creation des users par un Admin d'une commune
    public Utilisateur createUser(CreateUserRequest request) {
        // Récupère l'organisation de l'admin connecté
        Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Area commune = admin.getCommune();

       if(!request.getEmail().contains("@") || !request.getEmail().contains(".") ){
        throw new RuntimeException("Your Email must be Correct!!!!!");
       }
        //mise en place du role
        Role roleUser  = new Role();
        roleUser.setLibele(TypeRole.USER);

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
        return this.utilisateurRepository.save(newUser);
    }


// connaitre l'utilisateur connecté
    public UserResponse getCurrentUser() {
      Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if(user==null){throw new RuntimeException("NOBODY CONNECTED");}
      return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .communeName(user.getCommune().getNameCommune())
            .build();
    }


}