package com.saasdemo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Wedding;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface WeddingRepository extends JpaRepository<Wedding,Long>{

  List<Wedding>  findByNumeroCertificatMariageAndCommune(String num, Area commune);

  List<Wedding> findAllByCommune(Area commune);

  Wedding findByIdAndCommune(Long id, Area commune);

  Wedding findByNumeroCertificatMariage(String numeroCertificatMariage);

  void deleteByIdAndCommune(Long magic, Area commune);



  
  /*compter le nombre de certificat */
  Long  countByCommune(Area commune);

  Wedding findByEmailAndCommune(String magic, Area commune);

  void deleteByEmailAndCommune(String magic, Area commune);
    
}