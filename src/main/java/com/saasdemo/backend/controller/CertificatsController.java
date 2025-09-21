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


/**
 * Controller for managing birth certificates (extrait de naissance) in the ETAT CIVIL application.
 * Provides endpoints for creating, reading, updating, deleting, and printing certificates.
 */
@RestController
@Slf4j
@Tag(
  name = "CERTIFICATES_CONTROLLER   REST Api for ETAT CIVIL",
  description="CERTICATES_CONTROLLER  REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
public class CertificatsController {
  // Service for birth certificate operations
  private final CertificatServices certificatServices;
  // Service for PDF generation
  private final PdfService pdfService;

  /**
   * Constructor with required services.
   * @param certificatServices Service for birth certificate operations
   * @param pdfService Service for PDF generation
   */
  public CertificatsController( CertificatServices certificatServices, PdfService pdfService){
    this.certificatServices = certificatServices;
    this.pdfService = pdfService;
  }

  // Logger instance for logging
  private static final Logger logger = LoggerFactory.getLogger(CertificatsController.class);
  
  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/

  /**
   * Create a new birth certificate.
   * @param extrait DTO request containing birth certificate data
   * @return ResponseEntity containing operation result
   */
  @Operation(
    summary="REST API to create new birth certificate  into APP ETAT CIVIL",
    description = "REST API to create  new birth certificate  inside ETAT CIVIL App "
  )
  @PostMapping(path="/birthCertificatecreation")
  public ResponseEntity<ResponseDto> BirthCertificateCreation(@RequestBody BirthDtoRequest extrait ){
    logger.info("➡️ Request to create birth certificate: {}", extrait);
    ResponseEntity<ResponseDto> altris = this.certificatServices.BirthCreate(extrait);
    logger.info("✅ Birth certificate created: {}", altris.getBody());
    return altris;
  }

  /**
   * Print the birth certificate as a PDF.
   * @return ResponseEntity containing PDF bytes
   * @throws IOException if PDF generation fails
   */
  @Operation(
    summary="REST API to print birth certificate  into APP ETAT CIVIL",
    description = "REST API to print birth certificate  inside ETAT CIVIL App "
  )
  @GetMapping("/birthcertificatepdfprinting")
  public ResponseEntity<byte[]> getbirthcertificatePdf() throws IOException {
    logger.info("➡️ Request to generate birth certificate PDF");
    ByteArrayInputStream pdfStream = this.certificatServices.generateBirthCertificatepdfservice();
    byte[] pdfBytes = pdfStream.readAllBytes();
    logger.info("✅ Birth certificate PDF generated ({} bytes)", pdfBytes.length);
    return ResponseEntity.ok()
            //.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=subscription.pdf") // For displaying in browser
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=birthcertificate.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
  }

  /**
   * Update an existing birth certificate.
   * @param num Certificate number
   * @param request DTO request containing updated data
   * @return ResponseEntity containing updated birth certificate
   */
  @Operation(
    summary="REST API to update birth certificate  into APP ETAT CIVIL",
    description = "REST API to update birth certificate  inside ETAT CIVIL App "
  )
  @PutMapping("/updatebirth/{num}")
  public ResponseEntity<BirthDtoResponse> updateBirthCertificate(
        @PathVariable String num,
        @RequestBody BirthDtoRequest request) {
    logger.info("➡️ Request to update birth certificate N° {} with data {}", num, request);
    ResponseEntity<BirthDtoResponse> updated = certificatServices.updatebirthservice(num, request);
    logger.info("✅ Birth certificate updated N° {}", updated.getBody().getNumeroExtrait());
    return updated;
  }

  /**
   * Read all birth certificates or search by certificate number.
   * @param num Certificate number (optional)
   * @return Stream of birth certificate responses
   */
  @Operation(
    summary="REST API to get birth certificate  into APP ETAT CIVIL",
    description = "REST API to get birth certificate  inside ETAT CIVIL App "
  )
  @GetMapping(path="/getbirthcertificate")
  public Stream<BirthDtoResponse> ReadBirthCertificate(@RequestParam(required = false) String num){
    logger.info("➡️ Request to fetch birth certificates with num={}", num);
    Stream<BirthDtoResponse> readix = this.certificatServices.ReadBirth(num);
    logger.info("✅ Birth certificates fetched successfully");
    return readix;
  }

  /**
   * Read a birth certificate by its ID.
   * @param id The certificate ID
   * @return Optional birth certificate response
   */
  @Operation(
    summary="REST API to get birth certificate by id  into APP ETAT CIVIL",
    description = "REST API to get birth certificate by id inside ETAT CIVIL App "
  )
  @GetMapping(path="/getbirthcertificate/{id}")
  public Optional<BirthDtoResponse> ReadBirthCertificateById(@Valid @RequestParam(required = true) Long id ){
    logger.info("➡️ Request to fetch birth certificate by ID {}", id);
    Optional<BirthDtoResponse> bix = this.certificatServices.ReadBirthById(id);
    logger.info("✅ Birth certificate fetched by ID {} -> {}", id, bix);
    return bix;
  }

  /**
   * List birth certificates from a start date with pagination and sorting.
   * @param startDate Filter certificates from this date
   * @param page Page number (default 0)
   * @param size Page size (default 10)
   * @return ResponseEntity containing paginated birth certificate responses
   */
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
    // Get current user information
    Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    logger.info("➡️ Request to list birth certificates from {} for user commune={} (page={}, size={})", startDate, user.getCommune(), page, size);
    Pageable pageable = PageRequest.of(page, size, Sort.by("dateNaissance").ascending());
    Page<BirthDtoResponse> extraitsPage = certificatServices.getExtraitsFromStartDate(user.getCommune(), startDate, pageable);
    logger.info("✅ Birth certificates page fetched: {} elements", extraitsPage.getTotalElements());
    return ResponseEntity.ok(extraitsPage);
  }

  /**
   * Delete a birth certificate (admin only).
   * @return ResponseEntity containing operation result
   */
  @Operation(
    summary="REST API to delete birth certificate  into APP ETAT CIVIL",
    description = "REST API to delete  birth certificate  inside ETAT CIVIL App "
  )
  @DeleteMapping(path="/birthcertificatedeletion")
  @PreAuthorize("hasRole('ADMIN')")
  private ResponseEntity<ResponseDto> deathCertificateDeletion(){
    logger.warn("⚠️ Request to delete a birth certificate (Admin only)");
    ResponseEntity<ResponseDto> birthdelete= this.certificatServices.Birthdeletion();
    logger.info("✅ Birth certificate deleted: {}", birthdelete.getBody());
    return birthdelete;
  }

  /**
   * Delete a birth certificate by ID (admin only).
   * @param id The certificate ID
   * @return ResponseEntity containing operation result
   */
  @Operation(
    summary="REST API to delete birth certificate by id into APP ETAT CIVIL",
    description = "REST API to delete birth certificate by id inside ETAT CIVIL App "
  )
  @DeleteMapping(path="/birthcertificatedeletionbyid/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  private ResponseEntity<ResponseDto> BirthCertificateDeletionbyid(@Valid @RequestParam(required = true) Long id){
    logger.warn("⚠️ Request to delete birth certificate by ID {} (Admin only)", id);
    ResponseEntity<ResponseDto> bedix =this.certificatServices.Birthdeletionid(id);
    logger.info("✅ Birth certificate deleted by ID {} -> {}", id, bedix.getBody());
    return bedix;
  }
}
