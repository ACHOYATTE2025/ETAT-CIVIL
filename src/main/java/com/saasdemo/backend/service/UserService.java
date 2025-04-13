package com.saasdemo.backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.CreateUserRequest;
import com.saasdemo.backend.entity.Commune;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.Role;
import com.saasdemo.backend.repository.CommuneRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

@Service
public class UserService {
  private final UtilisateurRepository utilisateurRepository ;
    private final CommuneRepository communeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UtilisateurRepository utilisateurRepository, CommuneRepository communeRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.communeRepository = communeRepository;
        this.passwordEncoder = passwordEncoder;
    }



    //creation des users par un Admin d'une commune
    public Utilisateur createUser(CreateUserRequest request) {
        // Récupère l'organisation de l'admin connecté
        Utilisateur admin = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Commune commune = admin.getCommune();

        // Créer un nouvel utilisateur dans la même organisation
        Utilisateur newUser = Utilisateur.builder()
                .username(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Par défaut, l'utilisateur créé a le rôle USER
                .commune(commune)
                .build();

        return this.utilisateurRepository.save(newUser);
    }
    
}