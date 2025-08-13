package com.saasdemo.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.config.MagicID;
import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.entity.Birth;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.mapper.BirthDtoMapper;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.security.TenantContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class BirthService {
  private final BirthRepository extraitNaissanceRepository;
  private final RegistreRepository registreRepository;
  private final BirthDtoMapper birthDtoMapper;
  
 

  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/


    
  

    
  //CREER UN EXTRAIT DE NAISSANCE

  public ResponseEntity<?> BirthCreate(BirthDtoRequest extrait) {
     ResponseEntity XXX;
     Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     
     List<Birth> Xtrait = this.extraitNaissanceRepository.findByNumeroExtraitAndCommune(extrait.getNumeroExtrait(),usex.getCommune());
   
   if(Xtrait.isEmpty())
      {  
        // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);


          //enregistrer un extrait
          Birth extros = Birth.builder()
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
                                    .numeroExtrait(extrait.getNumeroExtrait()) 
                                    .professionMere(extrait.getProfessionMere())   
                                    .professionPere(extrait.getProfessionPere()) 
                                    .utilisateur(usex)
                                    .build();
            
          this.extraitNaissanceRepository.save(extros);
            XXX =  ResponseEntity.ok().body("EXTRAIT DE NAISSANCE "+extrait.getNumeroExtrait()+" EST CREE");}
    else{  XXX = ResponseEntity.badRequest().body("EXTRAIT DE NAISSANCE EST DEJA ENREGISTRE");
     }
     
    return XXX;}
    
  

  



//MODIFIER UN EXTRAIT
  public ResponseEntity<?> UpdateBirth(BirthDtoRequest extrait, Long idx) {
    ResponseEntity TTT =null;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Birth Xtrait = (Birth) this.extraitNaissanceRepository.findByIdAndCommune(idx,usex.getCommune());
    

   
  
    if(Xtrait==null){TTT = ResponseEntity.badRequest().body("EXTRAIT INCONNU");}
    else{
     /* Xtrail.setCarnet(extrait.getCarnet());
      Xtrail.setCni1(extrait.getCni1());
      Xtrail.setCni2(extrait.getCni2());*/
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
      Xtrait.setNumeroExtrait(extrait.getNumeroExtrait());
      Xtrait.setProfessionMere(extrait.getProfessionMere());
      Xtrait.setProfessionMere(extrait.getProfessionMere());
      Xtrait.setProfessionPere(extrait.getProfessionPere());
      this.extraitNaissanceRepository.save(Xtrait);
      TTT=ResponseEntity.ok().body("EXTRAIT "+ Xtrait.getNumeroExtrait()+ " A ETE MODIFIEE AVEC SUCCES");
  }
    return TTT;}
  

    


// Lire un extrait ou les extraits
public Stream<BirthDtoResponse> ReadBirth(String num) {
  
  boolean notEmpty = Strings.isNotEmpty(num);
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
  if(notEmpty)
      { Birth THOR = this.extraitNaissanceRepository.findByNumeroExtrait(num);
        if(THOR==null){throw new RuntimeException("EXTRAIT DE NAISSANCE INEXISTANT");}
       MagicID.magic=THOR.getId();
       return this.extraitNaissanceRepository.findByNumeroExtraitAndCommune(num,usex.getCommune())
               .stream()
               .map(birthDtoMapper);}
    return this.extraitNaissanceRepository.findAllByCommune(usex.getCommune())
            .stream()
            .map(birthDtoMapper);}




//suprimmer un extrait
@Transactional
public String Birthdeletion() {
  Birth DEXA;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  try{
    Birth Xtrait = this.extraitNaissanceRepository.findByIdAndCommune( MagicID.magic, usex.getCommune());
    DEXA=Xtrait;
  }
   catch(Exception e){throw new RuntimeException("SUPPRESION IMPOSSIBLE-EXTRAIT DE NAISSANCE INTROUVABLE");}
     log.info("XTRAIT :"+DEXA);
     this.extraitNaissanceRepository.deleteByIdAndCommune(MagicID.magic, usex.getCommune());
     return "EXTRAIT DE NAISSANCE N° "+DEXA.getNumeroExtrait()+" A ETE SUPPRIME" ;}
}

   


  








