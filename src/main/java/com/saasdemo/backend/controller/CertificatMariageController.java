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

import com.saasdemo.backend.dto.CertificatDto;
import com.saasdemo.backend.entity.CertificatMariage;
import com.saasdemo.backend.repository.CertificatMariageRepository;
import com.saasdemo.backend.service.CertificatMariageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CertificatMariageController {
  private final CertificatMariageService certificatMariageService;
  private final CertificatMariageRepository certificatMariageRepository;





  /*==============================================*/
  /*       Volet Certificat de mariage            */
  /*==============================================*/




 //creer un certificat de mariage
  @PostMapping(path="/certificatMariageCreation")
  public ResponseEntity<?> certifcatCreation(@Valid @RequestBody  CertificatDto certificat){
    return this.certificatMariageService.creerCertificat(certificat);
  }    



//Modifier un Certificat de Mariage
@PutMapping(path="/modifierCertificat/{id}")
private ResponseEntity<?> modifierCertificat(@Valid @RequestBody CertificatDto certificat, @PathVariable Long id){
  return this.certificatMariageService.modifierCertificat(certificat,id);
}



//lire tous les certificats ou chercher un certificat
@GetMapping(path="/lireUnOuTousCertificats")
 Optional<List<CertificatMariage>>  lireCertificat(@Valid @RequestParam(required = false)  String num){
  return  this.certificatMariageService.lireCertificats(num);
}



//suprimmer un extrait de naissance
@DeleteMapping(path="/supprimerCertificat")
@PreAuthorize("hasRole('ADMIN')")
private String supprimerCertificat(){
   return this.certificatMariageService.supprimerCertificat();
}
    
}