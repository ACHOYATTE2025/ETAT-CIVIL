package com.saasdemo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface BirthRepository extends JpaRepository<Birth,Long> {


  
  //List<Birth> findByNumeroExtraitAndCommune(String numero, Area commune);

  List<Birth> findAllByNumeroExtraitAndCommune(String numero, Area commune);
  
  Optional<Birth> findByNumeroExtraitAndCommune(String numeroExtrait, Area commune);


  List<Birth> findByEmailAndCommune(String numero, Area commune);
  
  List<Birth> findAllByEmailAndCommune(String email, Area commune);

  //avec pagination
    Page<BirthDtoResponse> findByCommune(Area commune, org.springframework.data.domain.Pageable pageable);
  
 


  Birth findByNumeroExtrait(String num);

  Birth findByIdAndCommune(Long id, Area commune);

  List<Birth> findAllByCommune(Area commune);

  String deleteByIdAndCommune(Long id, Area commune);
  

/*compter le nombre de certificat */
  Long  countByCommune(Area commune);

  void deleteByEmailAndCommune(String magic, Area commune);

  Optional<Birth> findByNumeroExtraitAndEmailAndCommune(String numerox, String email, Area commune);
  

}