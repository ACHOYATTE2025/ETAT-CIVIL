package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;

@Repository
public interface CommuneRepository extends CrudRepository<Area, Long> {

  Optional<Area>  findByNameCommune(String name);

  

 
    
}
