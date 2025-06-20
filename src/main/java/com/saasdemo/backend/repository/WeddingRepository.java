package com.saasdemo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Wedding;
import com.saasdemo.backend.entity.area;


@Repository
public interface WeddingRepository extends JpaRepository<Wedding,Long>{

  List<Wedding>  findByNumeroCertificatMariageAndCommune(String num, area commune);

  List<Wedding> findAllByCommune(area commune);

  Wedding findByIdAndCommune(Long id, area commune);

  Wedding findByNumeroCertificatMariage(String numeroCertificatMariage);

  void deleteByIdAndCommune(Long magic, area commune);



  
  /*compter le nombre de certificat */
  Long  countByCommune(area commune);
    
}