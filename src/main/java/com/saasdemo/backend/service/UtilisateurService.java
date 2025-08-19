package com.saasdemo.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UtilisateurService implements UserDetailsService {
    
    private final UtilisateurRepository utilisateurRepository;

   
    @Override
    public UserDetails loadUserByUsername(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
    }
}
