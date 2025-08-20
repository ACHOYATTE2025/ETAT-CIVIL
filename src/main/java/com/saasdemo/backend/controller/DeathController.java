package com.saasdemo.backend.controller;

import java.util.stream.Stream;

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

import com.saasdemo.backend.dto.DeathDtoRequest;
import com.saasdemo.backend.dto.DeathdtoResponse;
import com.saasdemo.backend.service.DeathService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeathController {
  private final DeathService deathService;



    /*==============================================*/
  /*       Volet Certificat de Deces                */
  /*==============================================*/




 //creer un certificat de mariage
  @PostMapping(path="/deathcertificatecreation")
  public ResponseEntity<?> deathCertificateCreation(@Valid @RequestBody  DeathDtoRequest certificat){
    return this.deathService.deathCreation(certificat);
  }    




//Modifier un Certificat de Mariage
@PutMapping(path="/updatedeathcertificate/{id}")
private ResponseEntity<?> updateDeathCertificate(@Valid @RequestBody DeathDtoRequest certificat, @PathVariable Long id){
  return this.deathService.updateDeath(certificat,id);
}



//lire tous les certificats ou chercher un certificat
@GetMapping(path="/readdeathcertificate")
Stream<DeathdtoResponse> readDeathCertificates(@Valid @RequestParam(required = false)  String num){
  return  this.deathService.readDeath(num);
}



//suprimmer un certifcat de deces
@DeleteMapping(path="/deathcertificatedeletion")
@PreAuthorize("hasRole('ADMIN')")
private String deathCertificateDeletion(){
   return this.deathService.deathDeletion();
}
    
}