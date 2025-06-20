package com.saasdemo.backend.controller;

import com.saasdemo.backend.dto.BirthDtoResponse;
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

import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.service.BirthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;


@RestController
@AllArgsConstructor
public class BirthController {
  private final BirthRepository extraitNaissanceRepository;
  private final BirthService birthService;


  


  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/

//Creer un Extrait de Naissance

@PostMapping(path="/BirthCertificateCreation")
private  ResponseEntity<?>  BirthCertificateCreation(@Valid @RequestBody BirthDtoRequest extrait ){
  return this.birthService.BirthCreate(extrait);
  }
    



//Modifier un Extrait de naissance
@PutMapping(path="/UpdateBirthCertificate/{id}")
private ResponseEntity<?> UpdateBirthCertificate(@RequestBody BirthDtoRequest extrait, @PathVariable Long id){
  return this.birthService.UpdateBirth(extrait,id);
}

//lire tous les extraits ou chercher un extrait
@GetMapping(path="/ReadBirthCertificate")
Stream <BirthDtoResponse> ReadBirthCertificate(@RequestParam(required = false)  String num){
  return  this.birthService.ReadBirth(num);
}

//suprimmer un extrait de naissance
@DeleteMapping(path="/BirthCertificateDeletion")
@PreAuthorize("hasRole('ADMIN')")
private String BirthCertificateDeletion(){
   return this.birthService.Birthdeletion();
}





}