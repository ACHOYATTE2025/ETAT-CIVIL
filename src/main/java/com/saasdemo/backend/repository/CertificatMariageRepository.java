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
    
}