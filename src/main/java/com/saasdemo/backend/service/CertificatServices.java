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



@Service
@RequiredArgsConstructor
@Slf4j
public class CertificatServices {
  private final BirthRepository birthRepository;
  private final RegistreRepository registreRepository;
  private final BirthDtoMapper birthDtoMapper;
  private final OperationSavingRepository OpSaving;
  private final PdfService pdfService;
  
 
  private Birth birth;
  private List<Birth> births;
  private Birth extros;
 

  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/


    
  

    
  //CREER UN EXTRAIT DE NAISSANCE

  public ResponseEntity<ResponseDto> BirthCreate(BirthDtoRequest extrait) {
     ResponseEntity<ResponseDto> XXX;
     Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     
     List<Birth> Xtrait = this.birthRepository.findByEmailAndCommune(extrait.getEmail(),usex.getCommune());
   
   if(Xtrait.isEmpty())
      {
        // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);

          //generation de numero d'extrait securitaire
          String numerox =UUID.randomUUID().toString();
          Optional<Birth> actubirth = this.birthRepository.findByNumeroExtraitAndEmailAndCommune(numerox,usex.getEmail(), usex.getCommune());

          if (actubirth.isPresent()){throw new RuntimeException("EXTRAIT N°"+ numerox + " EXISTE DEJA");}
          //enregistrer un extrait
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
         log.info("birth :"+altros);

             //save operation of register USER in OPeration saving
              OperationsSaving savingx  = OperationsSaving.builder()
                                          .name(usex.getUsername())
                                          .email(usex.getEmail())
                                          .operationNature(TypeOperation.CREER_UN_EXTRAIT_NAISSANCE)
                                          .operationDate(Instant.now())
                                          .utilisateur(usex)
                                          .NumeroActe(numerox)
                                          .build();
              this.OpSaving.save(savingx);

            XXX =  ResponseEntity
                  .status(HttpStatus.OK)
                  .body(new ResponseDto(200, "EXTRAIT DE NAISSANCE N° "+extros.getNumeroExtrait()+" EST CREE"));}
    else{  XXX = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body( new ResponseDto(406, "EXTRAIT DE NAISSANCE EST DEJA ENREGISTRE"));
     }
     
    return XXX;}
    
//=========================================================================================================================================================


//generer le pdf d'un certificat venant d'être créer
public ByteArrayInputStream generateBirthCertificatepdfservice(){
    return this.pdfService.generateBirthCertificatePdf(extros);
}

//=========================================================================================================================================================  



//MODIFIER UN EXTRAIT
  public ResponseEntity<BirthDtoResponse> updatebirthservice(String num,BirthDtoRequest extrait) {
   
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<Birth>  optionalXtrait =  this.birthRepository.findByNumeroExtraitAndCommune(num,usex.getCommune());
    
     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     log.info("tenantId :"+ TenantContext.getCurrentTenantId());
   
  
    if(optionalXtrait.isEmpty()){throw new RuntimeException("EXTRAIT INCONNU"); }
     // récupérer l'objet Birth réel
    Birth Xtrait = optionalXtrait.get();


    
     /* Xtrail.setCarnet(extrait.getCarnet());
      Xtrail.setCni1(extrait.getCni1());
      Xtrail.setCni2(extrait.getCni2());*/
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
      Birth  saved = this.birthRepository.save(Xtrait);

         //save operation of register USER in OPeration saving
        OperationsSaving savingx  = OperationsSaving.builder()
                                    .name(usex.getUsername())
                                    .email(usex.getEmail())
                                    .operationNature(TypeOperation.MODIFIER_UN_EXTRAIT_NAISSANCE)
                                    .operationDate(Instant.now())
                                    .utilisateur(usex)
                                    .NumeroActe(num)
                                    .build();
        this.OpSaving.save(savingx);

         // ✅ Ici : appeler apply sur l'instance injectée
        BirthDtoResponse response = birthDtoMapper.apply(saved);

      return ResponseEntity.ok(response);
  }
  
    

//=========================================================================================================================================================      

  
// Lire un extrait ou les extraits
public Stream<BirthDtoResponse> ReadBirth(String num) {
  
  boolean notEmpty = Strings.isNotEmpty(num);
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
  if(notEmpty)
      { birth = this.birthRepository.findByNumeroExtrait(num);
        if(birth==null){throw new RuntimeException("EXTRAIT DE NAISSANCE INEXISTANT");}
     
       return this.birthRepository.findByNumeroExtraitAndCommune(num,usex.getCommune())
              .stream()
              .map(birthDtoMapper);}
        births = (List<Birth>) this.birthRepository.findAllByCommune(usex.getCommune());
        if(births.isEmpty()){throw new RuntimeException( "AUCUN EXTRAIT DISPONIBLE") ; }
  return births.stream().map(birthDtoMapper);
          
  }

//=========================================================================================================================================================  

//lire un extrait par Id  
public Optional<BirthDtoResponse> ReadBirthById(Long id) {

  if(id==null){throw new RuntimeException("ID non Inséré");}
  Optional<Birth> birthd = this.birthRepository.findById(id);
  if(birthd.isEmpty()){throw new RuntimeException("EXTRAIT INEXISTANT!!!");}

  Optional<BirthDtoResponse> alex=  birthd.map(birthDtoMapper);
  return alex;
}



//===============================================================================================================
  //lire les extraits avec paginations et tri
  public Page<BirthDtoResponse> getExtraitsFromStartDate(Area commune, LocalDate startDate, Pageable pageable) {
        LocalDate endDate = LocalDate.now(); // date d'aujourd'hui
        return birthRepository
                .findByCommuneAndDateNaissanceBetween(commune, startDate, endDate, pageable)
                .map(birthDtoMapper);
    }

//=========================================================================================================================================================  


//suprimmer un extrait
@Transactional
public ResponseEntity<ResponseDto> Birthdeletion() {
  List<Birth> DEXA=null;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  
     if(birth== null){throw new RuntimeException( "SUPPRESSION IMPOSSIBLE") ; }
    List<Birth> births =  this.birthRepository.findByEmailAndCommune( birth.getEmail(), usex.getCommune());
    DEXA=births;
      if(births== null){throw new RuntimeException( "SUPPRESSION IMPOSSIBLE") ; }
 
     log.info("XTRAIT :"+DEXA);
     

        //save operation of register USER in OPeration saving
        OperationsSaving savingx  = OperationsSaving.builder()
                                    .name(usex.getUsername())
                                    .email(usex.getEmail())
                                    .operationNature(TypeOperation.SUPPRIMER_UN_EXTRAIT_NAISSANCE)
                                    .operationDate(Instant.now())
                                    .utilisateur(usex)
                                    .NumeroActe(birth.getNumeroExtrait())
                                    .build();
        this.OpSaving.save(savingx);

        //delete an birth document
        this.birthRepository.deleteByEmailAndCommune(birth.getEmail(), usex.getCommune());
      Birth actos = birth;
      birth =null;
     return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ResponseDto(406, "EXTRAIT DE NAISSANCE N° "+actos.getNumeroExtrait()+" A ETE SUPPRIME" ));
     }

//=========================================================================================================================================================  

//suprimmer un extrait by id
@Transactional
public ResponseEntity<ResponseDto> Birthdeletionid(Long id) {
  Birth DEXO=null;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  
     if(id== null){throw new RuntimeException( "SUPPRESSION IMPOSSIBLE") ; }
    Birth birtho =  this.birthRepository.findByIdAndCommune( id, usex.getCommune());
    DEXO=birtho;
      if(birtho== null){throw new RuntimeException( "SUPPRESSION IMPOSSIBLE") ; }
 
     log.info("XTRAIT :"+DEXO);
     

        //save operation of register USER in OPeration saving
        OperationsSaving savingx  = OperationsSaving.builder()
                                    .name(usex.getUsername())
                                    .email(usex.getEmail())
                                    .operationNature(TypeOperation.SUPPRIMER_UN_EXTRAIT_NAISSANCE)
                                    .operationDate(Instant.now())
                                    .utilisateur(usex)
                                    .NumeroActe(birtho.getNumeroExtrait())
                                    .build();
        this.OpSaving.save(savingx);

        //delete an birth document
        this.birthRepository.deleteByEmailAndCommune(birtho.getEmail(), usex.getCommune());
      Birth actas = birtho;
      birtho =null;
     return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ResponseDto(406, "EXTRAIT DE NAISSANCE N° "+actas.getNumeroExtrait()+" A ETE SUPPRIME" ));
     }






}

   


  








