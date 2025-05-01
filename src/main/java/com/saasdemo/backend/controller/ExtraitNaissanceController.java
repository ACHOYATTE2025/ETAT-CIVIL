package com.saasdemo.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.ExtraitDto;
import com.saasdemo.backend.entity.ExtraitNaissance;
import com.saasdemo.backend.repository.ExtraitNaissanceRepository;
import com.saasdemo.backend.service.ExtraitNaissanceService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
public class ExtraitNaissanceController {
  private ExtraitNaissanceRepository extraitNaissanceRepository;
  private final ExtraitNaissanceService documentMairieService;

  

//Creer un Extrait de Naissance

@PostMapping(path="/creerExtrait")
private  ResponseEntity<?>  createExtraitNaissance(@Valid @RequestBody   ExtraitDto extrait ){
  return this.documentMairieService.creerExtait(extrait);
  }
    



//Modifier un Extrait de naissance
@PutMapping(path="/modifierExtrait/{id}")
private ResponseEntity<?> modifierExtraitNaissance(@RequestBody ExtraitDto extrait, @PathVariable Long id){
  return this.documentMairieService.modifierExtrait(extrait,id);
}

//lire tous les extraits ou chercher un extrait
@GetMapping(path="/lireUnOuTousExtraits")
 Optional<List<ExtraitNaissance>>  lireExtrait(@RequestParam(required = false)  String num){
  return  this.documentMairieService.lireExtrait(num);
}

//suprimmer un extrait de naissance
@DeleteMapping(path="/supprimerExtrait")
@PreAuthorize("hasRole('ADMIN')")
private String supprimerextrait(){
   return this.documentMairieService.supprimerExtrait();
}
}