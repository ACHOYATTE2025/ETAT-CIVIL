package com.saasdemo.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;


@Repository
public interface BirthRepository extends JpaRepository<Birth,Long> {

  List<Birth> findByNumeroExtraitAndCommune(String numero, Area commune);

  List<Birth> findAllByNumeroExtraitAndCommune(String numero, Area commune);

  Birth findByNumeroExtrait(String num);

  Birth findByIdAndCommune(Long id, Area commune);

  List<Birth> findAllByCommune(Area commune);

  String deleteByIdAndCommune(Long id, Area commune);
  

/*compter le nombre de certificat */
  Long  countByCommune(Area commune);
  

    
}