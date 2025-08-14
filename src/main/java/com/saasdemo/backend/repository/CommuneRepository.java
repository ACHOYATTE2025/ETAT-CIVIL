package com.saasdemo.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Utilisateur;

@Repository
public interface CommuneRepository extends CrudRepository<Area, Long> {

  Area findByNameCommune(String name);

  void save(Utilisateur utilisateur);

 
    
}
