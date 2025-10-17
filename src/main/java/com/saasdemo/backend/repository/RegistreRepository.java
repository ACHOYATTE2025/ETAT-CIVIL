package com.saasdemo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Registre;

@Repository
public interface RegistreRepository extends JpaRepository<Registre,Long>{
    
}