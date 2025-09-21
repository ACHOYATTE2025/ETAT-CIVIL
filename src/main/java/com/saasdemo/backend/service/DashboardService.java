package com.saasdemo.backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.DashboardResponse;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.RoleRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final UtilisateurRepository utilisateurRepository;
  private final RoleRepository roleRepository;
  private final BirthRepository birthRepository;
 

  public DashboardResponse dash() {
    Utilisateur usx = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TenantContext.setCurrentTenantId(usx.getCommune().getId());

    Role roleAdmin = roleRepository.findByLibele(TypeRole.ADMIN)
        .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
    /*Role roleUser = roleRepository.findByLibele(TypeRole.USER)
        .orElseThrow(() -> new RuntimeException("Role USER not found"));*/

    // Counting
    Long nbreADMIN = utilisateurRepository.countByRoleAndCommuneId(roleAdmin, TenantContext.getCurrentTenantId());
    //Long nbreUSER = utilisateurRepository.countByRoleAndCommuneId(roleUser, TenantContext.getCurrentTenantId());

    Long nbreADMINACTIVE = utilisateurRepository.countByRoleAndCommuneAndActive(roleAdmin, usx.getCommune(), true);
    //Long nbreUSERACTIVE = utilisateurRepository.countByRoleAndCommuneAndActive(roleUser, usx.getCommune(), true);

    // Counting certificates
    Long nbreExtraitNaissance = birthRepository.countByCommune(usx.getCommune());

    // Return the response
    return DashboardResponse.builder()
        .nombreAdmin(nbreADMIN)
        //.nombreUser(nbreUSER)
        .nombreAdminActive(nbreADMINACTIVE)
        .nombreAdminDesactive(nbreADMIN - nbreADMINACTIVE)
       // .nombreUserActive(nbreUSERACTIVE)
        //.nombreUserDesactive(nbreUSER - nbreUSERACTIVE)
        .nombreExtraitNaissance(nbreExtraitNaissance)
        .build();
  }
}
