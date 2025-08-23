package com.saasdemo.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.config.MagicID;
import com.saasdemo.backend.dto.WeddingDtoRequest;
import com.saasdemo.backend.dto.WeddingDtoResponse;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Wedding;
import com.saasdemo.backend.mapper.WeddingDtoMapper;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.repository.WeddingRepository;
import com.saasdemo.backend.security.TenantContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeddingService {
  private final WeddingRepository certificatMariageRepository;
  private final RegistreRepository registreRepository;
  private final WeddingDtoMapper weddingDtoMapper;
  private  ResponseEntity XXX;





   /*==============================================*/
  /*       Volet Certificat de mariage            */
  /*==============================================*/

  //CREER UN CERTIFICAT DE MARIAGE
  public ResponseEntity<?> weddingCreation(WeddingDtoRequest certificat) {
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //configurer le tenant
    TenantContext.setCurrentTenantId(usex.getCommune().getId());
    List<Wedding> certi = this.certificatMariageRepository.findByNumeroCertificatMariageAndCommune(certificat.getNumeroCertificatMariage(),usex.getCommune());

    if(certi.isEmpty()){

      // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);
    
      Wedding certificamariage= Wedding.builder()
                                        .numeroCertificatMariage(certificat.getNumeroCertificatMariage()) 
                                        .commune(usex.getCommune())
                                        .dateDelivranceDocument(LocalDate.now())
                                        .dateMariage(certificat.getDateMariage())
                                        .dateNaissanceEpouse(certificat.getDateNaissanceEpouse())
                                        .dateNaissanceEpoux(certificat.getDateNaissanceEpoux())
                                        .domicileEpouse(certificat.getDomicileEpouse())
                                        .domicileEpoux(certificat.getDomicileEpoux())
                                        .lieuNaissanceEpouse(certificat.getLieuNaissanceEpouse())
                                        .lieuNaissanceEpoux(certificat.getDomicileEpoux())
                                        .nomEpouse(certificat.getNomEpouse())
                                        .nomEpoux(certificat.getNomEpoux())
                                        .nomPere(certificat.getNomPere())
                                        .nomMere(certificat.getNomMere())
                                        .nomEpoux(certificat.getNomEpoux())
                                        .nomPereEpouse(certificat.getNomPereEpouse())
                                        .nomMereEpouse(certificat.getNomMereEpouse())
                                        .professionEpouse(certificat.getProfessionEpouse())
                                        .professionEpoux(certificat.getNomEpoux())
                                        .regimeMariage(certificat.getRegimeMariage())
                                        .registre(regis)
                                        .utilisateur(usex)
                                        .build();
      this.certificatMariageRepository.save(certificamariage);
      XXX= ResponseEntity.ok().body("CERTIFICAT DE MARIAGE  N° "+ certificat.getNumeroCertificatMariage()+" EST CREE");}
    else{
      XXX = ResponseEntity.badRequest().body("EXTRAIT DE NAISSANCE EST DEJA ENREGISTRE");
    }
    return XXX; }




//modifier un certificat
  public ResponseEntity<?> updateWedding(WeddingDtoRequest certificat, Long id) {
    ResponseEntity TTT =null;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     Wedding certi = this.certificatMariageRepository.findByIdAndCommune(id,usex.getCommune());

       
   if(certi==null){TTT = ResponseEntity.status(HttpStatusCode.valueOf(403)).body("CERTIFICAT DE MARIAGE INCONNU");}
                      else{
                        certi.setCommune(usex.getCommune());
                        certi.setDateMariage(certificat.getDateMariage());
                        certi.setDateNaissanceEpouse(certificat.getDateNaissanceEpouse());
                        certi.setDateNaissanceEpoux(certificat.getDateNaissanceEpoux());
                        certi.setDomicileEpouse(certificat.getDomicileEpouse());
                        certi.setDomicileEpoux(certificat.getDomicileEpoux());
                        certi.setLieuNaissanceEpouse(certificat.getLieuNaissanceEpouse());
                        certi.setLieuNaissanceEpoux(certificat.getLieuNaissanceEpoux());
                        certi.setNomEpouse(certificat.getNomEpouse());
                        certi.setNomEpoux(certificat.getNomEpoux());
                        certi.setNomMere(certificat.getNomMere());
                        certi.setNomPere(certificat.getNomPere());
                        certi.setNomMere(certificat.getNomMere());
                        certi.setNomPere(certificat.getNomPere());
                        certi.setNomMereEpouse(certificat.getNomMereEpouse());
                        certi.setDateNaissanceEpoux(certificat.getDateNaissanceEpoux());
                        certi.setProfessionEpouse(certificat.getProfessionEpouse());
                        certi.setProfessionEpoux(certificat.getProfessionEpoux());
                        certi.setRegimeMariage(certificat.getRegimeMariage());
                        this.certificatMariageRepository.save(certi);
                        TTT=ResponseEntity.ok().body("CERTIFICAT "+ certi.getNumeroCertificatMariage()+ " A ETE MODIFIEE AVEC SUCCES");
  }
  return TTT;
}


//Lire un ou les certificats

  public Stream<WeddingDtoResponse> readWedding(String num) {
    boolean notEmpty = Strings.isNotEmpty(num);
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
    if(notEmpty )
      { Wedding THOR = this.certificatMariageRepository.findByNumeroCertificatMariage(num);
        if(THOR==null){throw new RuntimeException("CERTIFICAT DE MARIAGE INEXISTANT");}
       //MagicID.magic=THOR.getId();
       return this.certificatMariageRepository.findByNumeroCertificatMariageAndCommune(num,usex.getCommune())
               .stream()
               .map(weddingDtoMapper);}
    return this.certificatMariageRepository.findAllByCommune(usex.getCommune())
            .stream()
            .map(weddingDtoMapper);
    
  }



//suprrimer un certificat
@Transactional
  public String deleteWedding() {
    Wedding DEXA;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  try{
    Wedding Xtrait = this.certificatMariageRepository.findByEmailAndCommune( MagicID.magic, usex.getCommune());
    DEXA=Xtrait;
  }
   catch(Exception e){throw new RuntimeException("SUPPRESION IMPOSSIBLE-CERTIFICAT DE MARIAGE INTROUVABLE");}
     log.info("XTRAIT :"+DEXA);
     this.certificatMariageRepository.deleteByEmailAndCommune(MagicID.magic, usex.getCommune());
     return "CERTIFICAT DE MARIAGE N° "+DEXA.getNumeroCertificatMariage()+" A ETE SUPPRIME" ;
    
  }












}