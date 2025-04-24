package com.saasdemo.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.saasdemo.backend.entity.Tenant;
import com.saasdemo.backend.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

  private TenantRepository tenantRepository;

  //recuperer un tenant
   public Optional<Tenant> getTenantById(String tenantId) {
        return tenantRepository.findById(tenantId);
    }

  

  //suspendre un abonnement
    public void suspendreAbonnement(String tenantId) {
      tenantRepository.findById(tenantId).ifPresent(tenant -> {
          tenant.setId(tenantId);
          tenant.setName(tenant.getName());
          tenant.setAbonnementStatut("SUSPENDU");
          tenant.setAbonnementExpireLe(LocalDateTime.now());
          tenant.setActive(false);
          tenantRepository.save(tenant);
      });
  }


}