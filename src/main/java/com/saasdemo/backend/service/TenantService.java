package com.saasdemo.backend.service;

import java.time.LocalDate;
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

    //activer souscription
    public void activateSubscription(String tenantId) {
      Tenant tenant = tenantRepository.findById(tenantId)
          .orElseThrow(() -> new RuntimeException("Tenant non trouvé : " + tenantId));
      tenant.setId(tenantId);
      tenant.setName(tenant.getName());
      tenant.setActive(true);
      tenant.setAbonnementStatut("ACTIVE");
      tenant.setAbonnementExpireLe(LocalDate.now().plusMonths(12)); // abonnement de 12 mois

      tenantRepository.save(tenant);
  }


  //suspendre un abonnement
    public void suspendreAbonnement(String tenantId) {
      tenantRepository.findById(tenantId).ifPresent(tenant -> {
          tenant.setId(tenantId);
          tenant.setName(tenant.getName());
          tenant.setAbonnementStatut("SUSPENDU");
          tenant.setActive(false);
          tenantRepository.save(tenant);
      });
  }


}