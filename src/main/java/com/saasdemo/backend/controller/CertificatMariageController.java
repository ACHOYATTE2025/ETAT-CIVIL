package com.saasdemo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.CertificatDto;
import com.saasdemo.backend.service.CertificatMariageService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CertificatMariageController {
  private CertificatMariageService certificatMariageService;



  //creer un certificat de mariage
  @PostMapping(path="/certificatMariageCreation")
  public ResponseEntity<?> certifcatCreation(@RequestBody @Valid CertificatDto certificat){
    return this.certificatMariageService.creerCertificat(certificat);
  }    
}