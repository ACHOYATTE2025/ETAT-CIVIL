package com.saasdemo.backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.DashboardDto;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.Role;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final UtilisateurRepository utilisateurRepository;
 

  public DashboardDto dash() {
   Utilisateur usx = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TenantContext.setCurrentTenantId(usx.getCommune().getId());
   Long nbreADMIN = this.utilisateurRepository.countByRoleAndCommuneId(Role.ADMIN,TenantContext.getCurrentTenantId());
   Long  nbreUSER= this.utilisateurRepository.countByRoleAndCommuneId(Role.USER,TenantContext.getCurrentTenantId()); 
   Long nbreUSERACTIVE = this.utilisateurRepository.countByRoleAndCommuneAndActive(Role.USER,usx.getCommune(),usx.getActive());
   Long nbreADMINACTIVE = this.utilisateurRepository.countByRoleAndCommuneAndActive(Role.ADMIN,usx.getCommune(),usx.getActive());
   return   DashboardDto.builder()
                  .nombreAdmin(nbreADMIN)
                  .nombreUser(nbreUSER)
                  .nombreAdminActive(nbreADMINACTIVE)
                  .nombreAdminDesactive(nbreADMIN-nbreADMINACTIVE)
                  .nombreUserActive(nbreUSERACTIVE)
                  .nombreUserDesactive(nbreUSER-nbreUSERACTIVE)
                  .build();
  }
}