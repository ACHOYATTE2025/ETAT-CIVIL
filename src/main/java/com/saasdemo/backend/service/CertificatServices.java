package com.saasdemo.backend.service;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;
import com.saasdemo.backend.entity.OperationsSaving;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.TypeOperation;
import com.saasdemo.backend.mapper.BirthDtoMapper;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.OperationSavingRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.security.TenantContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling birth certificate operations.
 * Provides methods for creating, updating, reading, and deleting birth certificates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CertificatServices {
  private final BirthRepository birthRepository;
  private final RegistreRepository registreRepository;
  private final BirthDtoMapper birthDtoMapper;
  private final OperationSavingRepository OpSaving;
  private final PdfService pdfService;

  // Stores the current birth record being handled
  private Birth birth;
  // Stores a list of birth records
  private List<Birth> births;
  // Stores the most recent birth record created
  private Birth extros;

  /*==============================================*/
  /*         Birth Certificate Section            */
  /*==============================================*/

  /**
   * Create a birth certificate.
   * @param extrait BirthDtoRequest containing birth certificate details
   * @return ResponseEntity with ResponseDto indicating success or failure
   */
  public ResponseEntity<ResponseDto> BirthCreate(BirthDtoRequest extrait) {
     log.info("BirthCreate called with email: {}", extrait.getEmail());
     ResponseEntity<ResponseDto> XXX;
     Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

     // Set tenant context to user's commune
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     log.info("Tenant set to commune id: {}", usex.getCommune().getId());

     // Check if a birth certificate already exists for this email and commune
     List<Birth> Xtrait = this.birthRepository.findByEmailAndCommune(extrait.getEmail(),usex.getCommune());
     log.info("Existing birth certificates found: {}", Xtrait.size());

     if(Xtrait.isEmpty()) {
        // Create and save register
        Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
        this.registreRepository.save(regis);
        log.info("Registre saved for year: {}", regis.getRegistreAnnee());

        // Generate secure birth certificate number
        String numerox = UUID.randomUUID().toString();
        Optional<Birth> actubirth = this.birthRepository.findByNumeroExtraitAndEmailAndCommune(numerox, usex.getEmail(), usex.getCommune());

        if (actubirth.isPresent()) {
          log.error("Birth certificate number already exists: {}", numerox);
          throw new RuntimeException("BIRTH CERTIFICATE N°" + numerox + " ALREADY EXISTS");
        }

        // Save birth certificate
        extros = Birth.builder() 
                      .email(extrait.getEmail())
                      .commune(usex.getCommune())
                      .dateDelivrance(extrait.getDateDelivrance())
                      .dateNaissance(extrait.getDateNaissance())
                      .registre(regis)
                      .deces(extrait.getDeces())
                      .dissolutionMariage(extrait.getDissolutionMariage())
                      .domicileMere(extrait.getDomicileMere())
                      .domicilePere(extrait.getDomicilePere())
                      .lieuDelivrance(usex.getCommune().getNameCommune())
                      .lieuNaissance(extrait.getLieuNaissance())
                      .marie(extrait.getMarie())
                      .marieAvec(extrait.getMarieAvec())
                      .nationaliteMere(extrait.getDomicileMere())
                      .nationalitePere(extrait.getNationalitePere())
                      .nomComplet(extrait.getNomComplet())
                      .nomMere(extrait.getNomMere())
                      .nomPere(extrait.getNomPere())
                      .numeroDecisionDM(extrait.getNumeroDecisionDM())
                      .numeroExtrait(numerox) 
                      .professionMere(extrait.getProfessionMere())   
                      .professionPere(extrait.getProfessionPere()) 
                      .utilisateur(usex)
                      .build();
            
        Birth altros = this.birthRepository.save(extros);
        log.info("Birth certificate created: {}", altros);

        // Log operation for creating birth certificate
        OperationsSaving savingx = OperationsSaving.builder()
                                    .name(usex.getUsername())
                                    .email(usex.getEmail())
                                    .operationNature(TypeOperation.CREER_UN_EXTRAIT_NAISSANCE)
                                    .operationDate(Instant.now())
                                    .utilisateur(usex)
                                    .NumeroActe(numerox)
                                    .build();
        this.OpSaving.save(savingx);
        log.info("Operation logged: CREATE birth certificate, number: {}", numerox);

        XXX = ResponseEntity
                  .status(HttpStatus.OK)
                  .body(new ResponseDto(200, "BIRTH CERTIFICATE N° " + extros.getNumeroExtrait() + " CREATED"));
      } else {
        log.warn("Birth certificate already registered for email: {}", extrait.getEmail());
        XXX = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(406, "BIRTH CERTIFICATE ALREADY REGISTERED"));
      }
     
    log.info("BirthCreate result: status={}, body={}", XXX.getStatusCode(), XXX.getBody().toString());
    return XXX;
  }
    
//=========================================================================================================================================================

/**
 * Generate the PDF for a newly created birth certificate.
 * @return ByteArrayInputStream containing the PDF
 */
public ByteArrayInputStream generateBirthCertificatepdfservice() {
    log.info("Generating PDF for birth certificate: {}", extros != null ? extros.getNumeroExtrait() : "none");
    return this.pdfService.generateBirthCertificatePdf(extros);
}

//=========================================================================================================================================================  


/**
 * Update an existing birth certificate.
 * @param num Birth certificate number
 * @param extrait BirthDtoRequest containing updated details
 * @return ResponseEntity with BirthDtoResponse containing the updated certificate
 */
public ResponseEntity<BirthDtoResponse> updatebirthservice(String num, BirthDtoRequest extrait) {
    log.info("updatebirthservice called for number: {}", num);

    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<Birth> optionalXtrait = this.birthRepository.findByNumeroExtraitAndCommune(num, usex.getCommune());
    
    // Set tenant context
    TenantContext.setCurrentTenantId(usex.getCommune().getId());
    log.info("Tenant set to commune id: {}", TenantContext.getCurrentTenantId());
   
    if(optionalXtrait.isEmpty()) {
      log.error("Unknown birth certificate for number: {}", num);
      throw new RuntimeException("UNKNOWN BIRTH CERTIFICATE");
    }
    // Get the actual Birth object
    Birth Xtrait = optionalXtrait.get();

    // Update fields
    Xtrait.setNumeroExtrait(num);
    Xtrait.setCommune(usex.getCommune());
    Xtrait.setDateDelivrance(extrait.getDateDelivrance());
    Xtrait.setDateNaissance(extrait.getDateNaissance());
    Xtrait.setDeces(extrait.getDeces());
    Xtrait.setDissolutionMariage(extrait.getDissolutionMariage());
    Xtrait.setDomicileMere(extrait.getDomicileMere());
    Xtrait.setDomicilePere(extrait.getDomicilePere());
    Xtrait.setLieuDelivrance(extrait.getLieuDelivrance());
    Xtrait.setLieuNaissance(extrait.getLieuNaissance());
    Xtrait.setMarie(extrait.getMarie());
    Xtrait.setMarieAvec(extrait.getMarieAvec());
    Xtrait.setNationaliteMere(extrait.getNationaliteMere());
    Xtrait.setNationalitePere(extrait.getNationalitePere());
    Xtrait.setNomComplet(extrait.getNomComplet());
    Xtrait.setNomMere(extrait.getNomMere());
    Xtrait.setNomPere(extrait.getNomPere());
    Xtrait.setNumeroDecisionDM(extrait.getNumeroDecisionDM());
    Xtrait.setProfessionMere(extrait.getProfessionMere());
    Xtrait.setProfessionMere(extrait.getProfessionMere());
    Birth saved = this.birthRepository.save(Xtrait);

    log.info("Birth certificate updated: {}", saved);

    // Log operation for updating birth certificate
    OperationsSaving savingx = OperationsSaving.builder()
                                  .name(usex.getUsername())
                                  .email(usex.getEmail())
                                  .operationNature(TypeOperation.MODIFIER_UN_EXTRAIT_NAISSANCE)
                                  .operationDate(Instant.now())
                                  .utilisateur(usex)
                                  .NumeroActe(num)
                                  .build();
    this.OpSaving.save(savingx);
    log.info("Operation logged: UPDATE birth certificate, number: {}", num);

    // Map birth to response DTO
    BirthDtoResponse response = birthDtoMapper.apply(saved);

    log.info("updatebirthservice response prepared");
    return ResponseEntity.ok(response);
}
  
//=========================================================================================================================================================      

/**
 * Read one or more birth certificates by number.
 * @param num Birth certificate number (optional)
 * @return Stream of BirthDtoResponse
 */
public Stream<BirthDtoResponse> ReadBirth(String num) {
  log.info("ReadBirth called with num: {}", num);

  boolean notEmpty = Strings.isNotEmpty(num);
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
  if(notEmpty) {
    birth = this.birthRepository.findByNumeroExtrait(num);
    if(birth == null) {
      log.error("Birth certificate not found for number: {}", num);
      throw new RuntimeException("BIRTH CERTIFICATE NOT FOUND");
    }
    log.info("Birth certificate found for number: {}", num);
    return this.birthRepository.findByNumeroExtraitAndCommune(num, usex.getCommune())
              .stream()
              .map(birthDtoMapper);
  }
  births = (List<Birth>) this.birthRepository.findAllByCommune(usex.getCommune());
  if(births.isEmpty()) {
    log.warn("No birth certificates available for commune: {}", usex.getCommune().getNameCommune());
    throw new RuntimeException("NO CERTIFICATES AVAILABLE");
  }
  log.info("Birth certificates found: {}", births.size());
  return births.stream().map(birthDtoMapper);      
}

//=========================================================================================================================================================  

/**
 * Read a birth certificate by its ID.
 * @param id Birth certificate ID
 * @return Optional of BirthDtoResponse
 */
public Optional<BirthDtoResponse> ReadBirthById(Long id) {
  log.info("ReadBirthById called for id: {}", id);

  if(id == null) {
    log.error("ID not provided");
    throw new RuntimeException("ID NOT PROVIDED");
  }
  Optional<Birth> birthd = this.birthRepository.findById(id);
  if(birthd.isEmpty()) {
    log.error("Birth certificate not found for id: {}", id);
    throw new RuntimeException("BIRTH CERTIFICATE NOT FOUND");
  }

  Optional<BirthDtoResponse> alex = birthd.map(birthDtoMapper);
  log.info("Birth certificate found for id: {}", id);
  return alex;
}

//===============================================================================================================
/**
 * Get birth certificates from a start date, with pagination and sorting.
 * @param commune Area (commune) to filter
 * @param startDate Start date
 * @param pageable Pageable for pagination and sorting
 * @return Page of BirthDtoResponse
 */
public Page<BirthDtoResponse> getExtraitsFromStartDate(Area commune, LocalDate startDate, Pageable pageable) {
    LocalDate endDate = LocalDate.now(); // today's date
    log.info("getExtraitsFromStartDate called for commune: {}, startDate: {}, endDate: {}", commune.getNameCommune(), startDate, endDate);
    return birthRepository
            .findByCommuneAndDateNaissanceBetween(commune, startDate, endDate, pageable)
            .map(birthDtoMapper);
}

//=========================================================================================================================================================  

/**
 * Delete a birth certificate.
 * @return ResponseEntity with ResponseDto indicating success or failure
 */
@Transactional
public ResponseEntity<ResponseDto> Birthdeletion() {
  log.info("Birthdeletion called");
  List<Birth> DEXA = null;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  
  if(birth == null) {
    log.error("Deletion impossible: birth is null");
    throw new RuntimeException("DELETION IMPOSSIBLE");
  }
  List<Birth> births = this.birthRepository.findByEmailAndCommune(birth.getEmail(), usex.getCommune());
  DEXA = births;
  if(births == null) {
    log.error("Deletion impossible: births list is null");
    throw new RuntimeException("DELETION IMPOSSIBLE");
  }
 
  log.info("Certificates to delete: {}", DEXA);

  // Log operation for deleting birth certificate
  OperationsSaving savingx = OperationsSaving.builder()
                              .name(usex.getUsername())
                              .email(usex.getEmail())
                              .operationNature(TypeOperation.SUPPRIMER_UN_EXTRAIT_NAISSANCE)
                              .operationDate(Instant.now())
                              .utilisateur(usex)
                              .NumeroActe(birth.getNumeroExtrait())
                              .build();
  this.OpSaving.save(savingx);
  log.info("Operation logged: DELETE birth certificate, number: {}", birth.getNumeroExtrait());

  // Delete birth certificate document
  this.birthRepository.deleteByEmailAndCommune(birth.getEmail(), usex.getCommune());
  Birth actos = birth;
  birth = null;
  log.info("Birth certificate deleted: number={}", actos.getNumeroExtrait());
  return ResponseEntity
          .status(HttpStatus.OK)
          .body(new ResponseDto(406, "BIRTH CERTIFICATE N° " + actos.getNumeroExtrait() + " HAS BEEN DELETED"));
}

//=========================================================================================================================================================  

/**
 * Delete a birth certificate by its ID.
 * @param id Birth certificate ID
 * @return ResponseEntity with ResponseDto indicating success or failure
 */
@Transactional
public ResponseEntity<ResponseDto> Birthdeletionid(Long id) {
  log.info("Birthdeletionid called for id: {}", id);
  Birth DEXO = null;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  
  if(id == null) {
    log.error("Deletion impossible: id is null");
    throw new RuntimeException("DELETION IMPOSSIBLE");
  }
  Birth birtho = this.birthRepository.findByIdAndCommune(id, usex.getCommune());
  DEXO = birtho;
  if(birtho == null) {
    log.error("Deletion impossible: birth certificate not found for id: {}", id);
    throw new RuntimeException("DELETION IMPOSSIBLE");
  }
 
  log.info("Certificate to delete: {}", DEXO);

  // Log operation for deleting birth certificate
  OperationsSaving savingx = OperationsSaving.builder()
                              .name(usex.getUsername())
                              .email(usex.getEmail())
                              .operationNature(TypeOperation.SUPPRIMER_UN_EXTRAIT_NAISSANCE)
                              .operationDate(Instant.now())
                              .utilisateur(usex)
                              .NumeroActe(birtho.getNumeroExtrait())
                              .build();
  this.OpSaving.save(savingx);
  log.info("Operation logged: DELETE birth certificate, number: {}", birtho.getNumeroExtrait());

  // Delete birth certificate document
  this.birthRepository.deleteByEmailAndCommune(birtho.getEmail(), usex.getCommune());
  Birth actas = birtho;
  birtho = null;
  log.info("Birth certificate deleted for id: {}", id);
  return ResponseEntity
          .status(HttpStatus.OK)
          .body(new ResponseDto(406, "BIRTH CERTIFICATE N° " + actas.getNumeroExtrait() + " HAS BEEN DELETED"));
}

}
