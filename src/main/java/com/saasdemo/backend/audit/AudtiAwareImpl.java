package com.saasdemo.backend.audit;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("auditAwareImpl")
public class AudtiAwareImpl implements AuditorAware<String>{

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of("SAAS");
  }

  

 
    
}