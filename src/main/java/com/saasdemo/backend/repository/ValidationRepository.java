package com.saasdemo.backend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {
    Optional<Validation> findByCode(String code);
    Optional<Validation> deleteByCode(String code);
    Optional<Validation> findById(long id);
    Optional<Validation> findByUtilisateur(Utilisateur utilisateur);
}
