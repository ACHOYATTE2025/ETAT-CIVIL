package com.saasdemo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.CertificatDeces;
import com.saasdemo.backend.entity.Commune;

@Repository
public interface CertificatDecesRepository extends JpaRepository<CertificatDeces,Long> {

  Optional<List<CertificatDeces>> findByNumeroCertificatAndCommune(String numeroCertificat, Commune commune);
  Optional<List<CertificatDeces>> findALLByNumeroCertificatAndCommune(String numeroCertificat, Commune commune);

  CertificatDeces findByIdAndCommune(Long id, Commune commune);

  CertificatDeces findByNumeroCertificat(String num);

  Optional<List<CertificatDeces>> findAllByCommune(Commune commune);

  void deleteByIdAndCommune(Long magic, Commune commune);


    
}