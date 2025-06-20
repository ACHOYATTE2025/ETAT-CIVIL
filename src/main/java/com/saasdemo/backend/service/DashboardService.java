package com.saasdemo.backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.DashboardResponse;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.Role;
import com.saasdemo.backend.repository.DeathRepository;
import com.saasdemo.backend.repository.WeddingRepository;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final UtilisateurRepository utilisateurRepository;
  private final DeathRepository certificatDecesRepository;
  private final WeddingRepository certificatMariageRepository;
  private final BirthRepository extraitNaissanceRepository;
 

  public DashboardResponse dash() {
   Utilisateur usx = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TenantContext.setCurrentTenantId(usx.getCommune().getId());
   Long nbreADMIN = this.utilisateurRepository.countByRoleAndCommuneId(Role.ADMIN,TenantContext.getCurrentTenantId());
   Long  nbreUSER= this.utilisateurRepository.countByRoleAndCommuneId(Role.USER,TenantContext.getCurrentTenantId()); 
   Long nbreUSERACTIVE = this.utilisateurRepository.countByRoleAndCommuneAndActive(Role.USER,usx.getCommune(),usx.getActive());
   Long nbreADMINACTIVE = this.utilisateurRepository.countByRoleAndCommuneAndActive(Role.ADMIN,usx.getCommune(),usx.getActive());
   Long nbreCertificatDeces = this.certificatDecesRepository.countByCommune(usx.getCommune());
   Long nbreCertificatMariage = this.certificatMariageRepository.countByCommune(usx.getCommune());
   Long nbreExtraitNaissance = this.extraitNaissanceRepository.countByCommune(usx.getCommune());
   return   DashboardResponse.builder()
                  .nombreAdmin(nbreADMIN)
                  .nombreUser(nbreUSER)
                  .nombreAdminActive(nbreADMINACTIVE)
                  .nombreAdminDesactive(nbreADMIN-nbreADMINACTIVE)
                  .nombreUserActive(nbreUSERACTIVE)
                  .nombreUserDesactive(nbreUSER-nbreUSERACTIVE)
                  .nombreCertificatDeces(nbreCertificatDeces)
                  .nombreCertificatMariage(nbreCertificatMariage)
                  .nombreExtraitNaissance(nbreExtraitNaissance)
                  .build();
  }
}