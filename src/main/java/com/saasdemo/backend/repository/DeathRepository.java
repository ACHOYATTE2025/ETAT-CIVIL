package com.saasdemo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Death;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface DeathRepository extends JpaRepository<Death,Long> {

  List<Death> findByNumeroCertificatAndCommune(String numeroCertificat, Area commune);
  List<Death> findAllByCommune(Area commune);

  Death findByIdAndCommune(Long id, Area commune);

  Death findByNumeroCertificat(String num);



  void deleteByIdAndCommune(Long magic, Area commune);


  /*compter le nombre de certificat */
  Long  countByCommune(Area commune);
  Death findByEmailAndCommune(String magic, Area commune);
  void deleteByEmailAndCommune(String magic, Area commune);


    
}