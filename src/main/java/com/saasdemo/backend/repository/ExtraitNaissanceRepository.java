package com.saasdemo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Commune;
import com.saasdemo.backend.entity.ExtraitNaissance;


@Repository
public interface ExtraitNaissanceRepository extends JpaRepository<ExtraitNaissance,Long> {

  Optional<List<ExtraitNaissance>> findByNumeroExtraitAndCommune(String numero,Commune commune);

  Optional<List<ExtraitNaissance>> findAllByNumeroExtraitAndCommune(String numero,Commune commune);

  ExtraitNaissance findByNumeroExtrait(String num);

  ExtraitNaissance findByIdAndCommune(Long id,Commune commune);

  Optional <List<ExtraitNaissance>> findAllByCommune(Commune commune);

  String deleteByIdAndCommune(Long id,Commune commune);
  


  

    
}