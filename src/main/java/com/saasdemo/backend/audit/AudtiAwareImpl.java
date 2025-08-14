package com.saasdemo.backend.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.saasdemo.backend.entity.Utilisateur;

@Component("auditAwareImpl")
public class AudtiAwareImpl implements AuditorAware<String>{
    Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(user.getCommune().getNameCommune());
  }

  

 
    
}