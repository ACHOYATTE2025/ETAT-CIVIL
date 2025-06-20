package com.saasdemo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Death;
import com.saasdemo.backend.entity.area;

@Repository
public interface DeathRepository extends JpaRepository<Death,Long> {

  List<Death> findByNumeroCertificatAndCommune(String numeroCertificat, area commune);
  List<Death> findAllByCommune(area commune);

  Death findByIdAndCommune(Long id, area commune);

  Death findByNumeroCertificat(String num);



  void deleteByIdAndCommune(Long magic, area commune);


  /*compter le nombre de certificat */
  Long  countByCommune(area commune);


    
}