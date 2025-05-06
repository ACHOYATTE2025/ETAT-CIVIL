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

import com.saasdemo.backend.dto.CertificatDecesDto;
import com.saasdemo.backend.entity.CertificatDeces;
import com.saasdemo.backend.service.CertificatDecesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CertificatDecesController {
  private final CertificatDecesService certificatDecesService;



    /*==============================================*/
  /*       Volet Certificat de Deces                */
  /*==============================================*/




 //creer un certificat de mariage
  @PostMapping(path="/certificatDecesCreation")
  public ResponseEntity<?> certifcatDecesCreation(@Valid @RequestBody  CertificatDecesDto certificat){
    return this.certificatDecesService.creerCertificatDeces(certificat);
  }    




//Modifier un Certificat de Mariage
@PutMapping(path="/modifierCertificatDeces/{id}")
private ResponseEntity<?> modifierCertificat(@Valid @RequestBody CertificatDecesDto certificat, @PathVariable Long id){
  return this.certificatDecesService.modifierCertificatDeces(certificat,id);
}



//lire tous les certificats ou chercher un certificat
@GetMapping(path="/lireUnOuTousCertificatsDeces")
 Optional<List<CertificatDeces>>  lireCertificatDeces(@Valid @RequestParam(required = false)  String num){
  return  this.certificatDecesService.lireCertificatDeces(num);
}



//suprimmer un certifcat de deces
@DeleteMapping(path="/deces")
@PreAuthorize("hasRole('ADMIN')")
private String supprimerCertificat(){
   return this.certificatDecesService.supprimerCertificatDeces();
}
    
}