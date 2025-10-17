package com.saasdemo.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
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
import com.saasdemo.backend.service.CertificatServices;
import com.saasdemo.backend.service.PdfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j


@Tag(
  name = "CERTIFICATES_CONTROLLER   REST Api for ETAT CIVIL",
  description="CERTICATES_CONTROLLER  REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
public class CertificatsController {
  private final CertificatServices certificatServices;
  private final PdfService pdfService;


  public CertificatsController( CertificatServices certificatServices, PdfService pdfService){
    this.certificatServices = certificatServices;
    this.pdfService = pdfService;
  }

    private static final Logger logger = LoggerFactory.getLogger(CertificatsController.class);
  


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
  ResponseEntity<ResponseDto> altris=this.certificatServices.BirthCreate(extrait);
      logger.info("birthcreation :"+altris);
      return altris;
  }

//imprimer l'extrait de naissance crée
 @Operation(
    summary="REST API to print birth certificate  into APP ETAT CIVIL",
    description = "REST API to print birth certificate  inside ETAT CIVIL App "
  )
@GetMapping("/birthcertificatepdfprinting")
public ResponseEntity<byte[]> getbirthcertificatePdf() throws IOException {
    ByteArrayInputStream pdfStream = this.certificatServices.generateBirthCertificatepdfservice();

    byte[] pdfBytes = pdfStream.readAllBytes();
    logger.info("certificate printing "+ pdfBytes);
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

    ResponseEntity<BirthDtoResponse> updated = certificatServices.updatebirthservice(num, request);
     logger.info("birth certificate updated N° "+ updated.getBody().getNumeroExtrait());
    return updated;
}


//lire tous les extraits ou chercher un extrait
 @Operation(
    summary="REST API to get birth certificate  into APP ETAT CIVIL",
    description = "REST API to get birth certificate  inside ETAT CIVIL App "
  )
@GetMapping(path="/getbirthcertificate")
Stream <BirthDtoResponse> ReadBirthCertificate(@RequestParam(required = false)  String num){

    Stream<BirthDtoResponse> readix = this.certificatServices.ReadBirth(num);
     logger.info("certificate fetch N° "+ readix);
     return readix;
}


//lire un extrait de naissance par Id
 @Operation(
    summary="REST API to get birth certificate by id  into APP ETAT CIVIL",
    description = "REST API to get birth certificate by id inside ETAT CIVIL App "
  )
@GetMapping(path="/getbirthcertificate/{id}")
Optional<BirthDtoResponse> ReadBirthCertificateById(@Valid @RequestParam(required = true)Long id ){
  Optional<BirthDtoResponse> bix = this.certificatServices.ReadBirthById(id);
  logger.info("birth certificate fetch by id N° "+ bix);
  return bix;
}

//lire les extraits avec tri ete pagination
 @Operation(
    summary="REST API to get birth certificate by page,size,birthdate into APP ETAT CIVIL",
    description = "REST API to get birth certificate by page,size,birthdate inside ETAT CIVIL App "
  )
@GetMapping("/listBirthCertificatesFromDate")
    public ResponseEntity<Page<BirthDtoResponse>> listBirthCertificatesFromDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateNaissance").ascending());

        Page<BirthDtoResponse> extraitsPage = certificatServices.getExtraitsFromStartDate(user.getCommune(), startDate, pageable);

        return ResponseEntity.ok(extraitsPage);
    }



//suprimmer un certifcat de naissance
 @Operation(
    summary="REST API to delete birth certificate  into APP ETAT CIVIL",
    description = "REST API to delete  birth certificate  inside ETAT CIVIL App "
  )
@DeleteMapping(path="/birthcertificatedeletion")
@PreAuthorize("hasRole('ADMIN')")
private  ResponseEntity<ResponseDto>  deathCertificateDeletion(){
    ResponseEntity<ResponseDto> birthdelete= this.certificatServices.Birthdeletion();
    logger.info("birth certificate deleted  "+ birthdelete.getBody());
    return birthdelete;
}

//suprimmer un extrait de naissance par Id
 @Operation(
    summary="REST API to delete birth certificate by id into APP ETAT CIVIL",
    description = "REST API to delete birth certificate by id inside ETAT CIVIL App "
  )
@DeleteMapping(path="/birthcertificatedeletionbyid/{id}")
@PreAuthorize("hasRole('ADMIN')")
private ResponseEntity<ResponseDto> BirthCertificateDeletionbyid(@Valid @RequestParam(required = true)Long id){
    ResponseEntity<ResponseDto> bedix =this.certificatServices.Birthdeletionid(id);
    logger.info("bith certificate deleted by id "+ bedix.getBody());
    return bedix;
}





}