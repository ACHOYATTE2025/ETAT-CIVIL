package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;



@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {

  Optional<Utilisateur> findByEmail(String email);


  Long countByCommuneId(Long orgId);

  
  //compter admin ou user selon la commune
  long countByRoleAndCommuneId(Role role,long id);

  //compter le nombre d'admin actif selon la commune
  long countByRoleAndCommuneAndActive(Role  role, Area commune, Boolean vrai);
  
  
  


}
