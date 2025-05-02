package com.saasdemo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.CertificatMariage;
import com.saasdemo.backend.entity.Commune;


@Repository
public interface CertificatMariageRepository extends JpaRepository<CertificatMariage,Long>{

  Optional <List<CertificatMariage>>  findByNumeroCertificatMariageAndCommune(String num,Commune commune);

  Optional <List<CertificatMariage>>  findAllByNumeroCertificatMariageAndCommune(String num,Commune commune);

  CertificatMariage findByIdAndCommune(Long id,Commune commune);

  CertificatMariage  findByNumeroCertificatMariage(String numeroCertificatMariage);

  void deleteByIdAndCommune(Long magic, Commune commune);

  Optional<List<CertificatMariage>> findAllByCommune(Commune commune);
    
}