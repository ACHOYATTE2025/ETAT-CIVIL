package com.saasdemo.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.saasdemo.backend.service.PdfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j


@Tag(
  name = "BIRTH_CONTROLLER   REST Api for ETAT CIVIL",
  description="BIRTH_CONTROLLER  REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
public class BirthController {
  private final BirthService birthService;
  private final PdfService pdfService;


  public BirthController( BirthService birthService, PdfService pdfService){
    this.birthService = birthService;
    this.pdfService = pdfService;
  }


  


  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/

//Creer un Extrait de Naissance
 @Operation(
    summary="REST API to create new birth certificate  into APP ETAT CIVIL",
    description = "REST API to create  new birth certificate  inside ETAT CIVIL App "
  )

@PostMapping(path="/birthCertificatecreation")
public  ResponseEntity<ResponseDto>  BirthCertificateCreation( @RequestBody BirthDtoRequest extrait ){
  ResponseEntity<ResponseDto> altris=this.birthService.BirthCreate(extrait);
  log.info("birthcreation :"+altris);
  return altris;
  }

//imprimer l'extrait de naissance crée
 @Operation(
    summary="REST API to print birth certificate  into APP ETAT CIVIL",
    description = "REST API to print birth certificate  inside ETAT CIVIL App "
  )
@GetMapping("/birthcertificatepdfprinting")
public ResponseEntity<byte[]> getbirthcertificatePdf() throws IOException {
    ByteArrayInputStream pdfStream = this.birthService.generateBirthCertificatepdfservice();

    byte[] pdfBytes = pdfStream.readAllBytes();

    return ResponseEntity.ok()
            //.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=subscription.pdf") voir le fichier dans le navigateur
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=birthcertificate.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}


//Modifier un Extrait de naissance
 @Operation(
    summary="REST API to update birth certificate  into APP ETAT CIVIL",
    description = "REST API to update birth certificate  inside ETAT CIVIL App "
  )
@PutMapping("/updatebirth/{num}")
public ResponseEntity<BirthDtoResponse> updateBirthCertificate(
        @PathVariable String num,
        @RequestBody BirthDtoRequest request) {

    ResponseEntity<BirthDtoResponse> updated = birthService.updatebirthservice(num, request);
    return updated;
}


//lire tous les extraits ou chercher un extrait
 @Operation(
    summary="REST API to get birth certificate  into APP ETAT CIVIL",
    description = "REST API to get birth certificate  inside ETAT CIVIL App "
  )
@GetMapping(path="/getbirthcertificate")
Stream <BirthDtoResponse> ReadBirthCertificate(@RequestParam(required = false)  String num){
  return  this.birthService.ReadBirth(num);
}


//lire un extrait de naissance par Id
 @Operation(
    summary="REST API to get birth certificate by id  into APP ETAT CIVIL",
    description = "REST API to get birth certificate by id inside ETAT CIVIL App "
  )
@GetMapping(path="/getbirthcertificate/{id}")
Stream <BirthDtoResponse> ReadBirthCertificateById(@Valid @RequestParam(required = true)Long id ){
  return  this.birthService.ReadBirthById(id);
}

//lire les extraits avec tri ete pagination
 @Operation(
    summary="REST API to get birth certificate by page,size,birthdate into APP ETAT CIVIL",
    description = "REST API to get birth certificate by page,size,birthdate inside ETAT CIVIL App "
  )
@GetMapping("/listbirthcertificatesByTri")
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



//suprimmer un certifcat de naissance
 @Operation(
    summary="REST API to delete birth certificate  into APP ETAT CIVIL",
    description = "REST API to delete  birth certificate  inside ETAT CIVIL App "
  )
@DeleteMapping(path="/birthcertificatedeletion")
@PreAuthorize("hasRole('ADMIN')")
private  ResponseEntity<ResponseDto>  deathCertificateDeletion(){
   return this.birthService.Birthdeletion();
}

//suprimmer un extrait de naissance par Id
 @Operation(
    summary="REST API to delete birth certificate by id into APP ETAT CIVIL",
    description = "REST API to delete birth certificate by id inside ETAT CIVIL App "
  )
@DeleteMapping(path="/birthcertificatedeletionbyid/{id}")
@PreAuthorize("hasRole('ADMIN')")
private ResponseEntity<ResponseDto> BirthCertificateDeletionbyid(@Valid @RequestParam(required = true)Long id){
   return this.birthService.Birthdeletionid(id);
}





}