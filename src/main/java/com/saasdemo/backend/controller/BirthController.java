package com.saasdemo.backend.controller;

import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.service.BirthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class BirthController {
  private final BirthService birthService;


  public BirthController( BirthService birthService){
    this.birthService = birthService;
  }


  


  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/

//Creer un Extrait de Naissance

@PostMapping(path="/BirthCertificateCreation")
public  ResponseEntity<ResponseDto>  BirthCertificateCreation(@Valid @RequestBody BirthDtoRequest extrait ){
  ResponseEntity<ResponseDto> altris=this.birthService.BirthCreate(extrait);
  log.info("birthcreation :"+altris);
  return altris;
  }




//Modifier un Extrait de naissance
@PutMapping(path="/UpdateBirthCertificate")
private ResponseEntity<ResponseDto> UpdateBirthCertificate(@RequestBody BirthDtoRequest extrait){
  return this.birthService.UpdateBirth(extrait);
}

//lire tous les extraits ou chercher un extrait
@GetMapping(path="/ReadBirthCertificate")
Stream <BirthDtoResponse> ReadBirthCertificate(@RequestParam(required = false)  String num){
  return  this.birthService.ReadBirth(num);
}

//lire les extraits avec tri ete pagination
@GetMapping("/ListBirthCertificatesByTri")
public ResponseEntity<Page<BirthDtoResponse>> listBirthCertificates(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "dateNaissance") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir) {

    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Page<BirthDtoResponse> extraitsPage = this.birthService.getExtraitsByCommune(usex.getCommune(), page, size, sortBy, sortDir);

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(extraitsPage);
}



//suprimmer un extrait de naissance
@DeleteMapping(path="/BirthCertificateDeletion")
//@PreAuthorize("hasRole('ADMIN')")
private ResponseEntity<ResponseDto> BirthCertificateDeletion(){
   return this.birthService.Birthdeletion();
}





}