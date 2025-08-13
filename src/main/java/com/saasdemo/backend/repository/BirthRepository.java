package com.saasdemo.backend.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.area;
import com.saasdemo.backend.entity.Birth;


@Repository
public interface BirthRepository extends JpaRepository<Birth,Long> {

  List<Birth> findByNumeroExtraitAndCommune(String numero, area commune);

  List<Birth> findAllByNumeroExtraitAndCommune(String numero, area commune);

  Birth findByNumeroExtrait(String num);

  Birth findByIdAndCommune(Long id, area commune);

  List<Birth> findAllByCommune(area commune);

  String deleteByIdAndCommune(Long id, area commune);
  

/*compter le nombre de certificat */
  Long  countByCommune(area commune);
  

    
}