package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.RefreshToken;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

  Optional <RefreshToken> findByValeur(String valeur);

  
    
}