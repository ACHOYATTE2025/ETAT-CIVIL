package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.List;

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
    
    
    private final UtilisateurRepository utilisateurRepository ;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final RoleRepository roleRepository;
    private final OperationSavingRepository OpSaving;

   
    @Override
    public UserDetails loadUserByUsername(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
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
       
        Role roleUser = new Role();
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

         //save operation of register USER in OPeration saving
        OperationsSaving savingx  = OperationsSaving.builder()
                                    .name(admin.getUsername())
                                    .email(admin.getEmail())
                                    .operationNature(TypeOperation.ENREGISTRER_UN_USER)
                                    .operationDate(Instant.now())
                                    .utilisateur(admin)
                                    .build();
        this.OpSaving.save(savingx);

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
