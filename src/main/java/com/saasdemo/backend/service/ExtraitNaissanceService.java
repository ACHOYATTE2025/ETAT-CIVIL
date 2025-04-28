package com.saasdemo.backend.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.config.MagicID;
import com.saasdemo.backend.dto.ExtraitDto;
import com.saasdemo.backend.entity.ExtraitNaissance;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.ExtraitNaissanceRepository;
import com.saasdemo.backend.security.TenantContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExtraitNaissanceService {
  private final ExtraitNaissanceRepository extraitNaissanceRepository;
  
  
 

  /*==============================================*/
  /*       Volet extrait de naissance             */
  /*==============================================*/


    


  
  

    
  //CREER UN EXTRAIT DE NAISSANCE

  public ResponseEntity<?> creerExtait(ExtraitDto extrait) {
     ResponseEntity XXX;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    

   Optional<List<ExtraitNaissance>> Xtrait = this.extraitNaissanceRepository.findByNumeroExtraitAndCommune(extrait.getNumeroExtrait(),usex.getCommune());
   System.out.println("Xtrait SIZE :"+Xtrait.get().size());
   if(Xtrait.get().size()==0)
      {
    
          ExtraitNaissance extros = ExtraitNaissance.builder()
                                    .commune(usex.getCommune())
                                    .dateDelivrance(extrait.getDateDelivrance())
                                    .dateNaissance(extrait.getDateNaissance())
                                    .dateRegistre(extrait.getDateRegistre())
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
                                    .build();
            
          this.extraitNaissanceRepository.save(extros);
            XXX =  ResponseEntity.ok().body("EXTRAIT DE NAISSANCE "+extrait.getNumeroExtrait()+" ENREGISTRE");}
    else{  XXX = ResponseEntity.badRequest().body("EXTRAIT DE NAISSANCE EST DEJA ENREGISTRE");
     }
     
    return XXX;}
    
  

  



//MODIFIER UN EXTRAIT
  public ResponseEntity<?> modifierExtrait(ExtraitDto extrait, Long idx) {
    ResponseEntity TTT =null;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ExtraitNaissance Xtrait = (ExtraitNaissance) this.extraitNaissanceRepository.findByIdAndCommune(idx,usex.getCommune());
    
  
    if(Xtrait==null){TTT = ResponseEntity.badRequest().body("EXTRAIT INCONNU");}
    else{
     /* Xtrail.setCarnet(extrait.getCarnet());
      Xtrail.setCni1(extrait.getCni1());
      Xtrail.setCni2(extrait.getCni2());*/
      Xtrait.setCommune(usex.getCommune());
      Xtrait.setDateDelivrance(extrait.getDateDelivrance());
      Xtrait.setDateNaissance(extrait.getDateNaissance());
      Xtrait.setDateRegistre(extrait.getDateRegistre());
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
public Optional<List<ExtraitNaissance>> lireExtrait(String num) {
  
  boolean notEmpty = Strings.isNotEmpty(num);
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
  if(notEmpty)
      { ExtraitNaissance THOR = this.extraitNaissanceRepository.findByNumeroExtrait(num);
       MagicID.magic=THOR.getId();
       return this.extraitNaissanceRepository.findByNumeroExtraitAndCommune(num,usex.getCommune());}
    return this.extraitNaissanceRepository.findAllByCommune(usex.getCommune());}




//suprimmer un extrait
@Transactional
public String supprimerExtrait() {
  ExtraitNaissance DEXA;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  try{
    ExtraitNaissance Xtrait = this.extraitNaissanceRepository.findByIdAndCommune( MagicID.magic, usex.getCommune());
    DEXA=Xtrait;
  }
   catch(Exception e){throw new RuntimeException("SUPPRESION IMPOSSIBLE-EXTRAIT DE NAISSANCE INTROUVABLE");}
     log.info("XTRAIT :"+DEXA);
     this.extraitNaissanceRepository.deleteByIdAndCommune(MagicID.magic, usex.getCommune());
     return "EXTRAIT DE NAISSANCE N° "+DEXA.getNumeroExtrait()+" A ETE SUPPRIME" ;}
}

   


  








