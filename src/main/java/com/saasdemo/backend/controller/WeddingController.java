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

import com.saasdemo.backend.dto.WeddingDtoRequest;
import com.saasdemo.backend.dto.WeddingDtoResponse;
import com.saasdemo.backend.service.WeddingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WeddingController {
  private final WeddingService weddingService;






  /*==============================================*/
  /*       Volet Certificat de mariage            */
  /*==============================================*/




 //creer un certificat de mariage
  @PostMapping(path="/weddingcertificatecreation")
  public ResponseEntity<?> weddingCertificateCreation(@Valid @RequestBody WeddingDtoRequest certificat){
    return this.weddingService.weddingCreation(certificat);
  }    



//Modifier un Certificat de Mariage
@PutMapping(path="/updatewedding/{id}")
private ResponseEntity<?> updatewedding(@Valid @RequestBody WeddingDtoRequest certificat, @PathVariable Long id){
  return this.weddingService.updateWedding(certificat,id);
}



//lire tous les certificats ou chercher un certificat
@GetMapping(path="/readweddingcertificate")
Stream<WeddingDtoResponse> readweddingcertificate(@Valid @RequestParam(required = false)  String num){
  return  this.weddingService.readWedding(num);
}



//suprimmer un extrait de naissance
@DeleteMapping(path="/weddingdeletion")
@PreAuthorize("hasRole('ADMIN')")
private String weddingDeletion(){
   return this.weddingService.deleteWedding();
}
    
}