package com.saasdemo.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.config.MagicID;
import com.saasdemo.backend.dto.DeathDtoRequest;
import com.saasdemo.backend.dto.DeathdtoResponse;
import com.saasdemo.backend.entity.Death;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.mapper.DeathDtoMapper;
import com.saasdemo.backend.repository.DeathRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeathService {
  private final DeathRepository certificatDecesRepository;
  private final RegistreRepository registreRepository;
  private final DeathDtoMapper deathMapperDto;



  
  /*==============================================*/
  /*       Volet Certificat de Deces           */
  /*==============================================*/
  
   
  
  //creation certificat deces
  public ResponseEntity<?> deathCreation(DeathDtoRequest certificat) {
    ResponseEntity XXX;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    //configurer le tenant
    TenantContext.setCurrentTenantId(usex.getCommune().getId());

    List<Death> certi = (List<Death>) this.certificatDecesRepository.findByNumeroCertificatAndCommune(certificat.getNumeroCertificat(),usex.getCommune());

    if(certi.isEmpty()){

      // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);
    
      Death certificatdeces= Death.builder()
                                       .commune(usex.getCommune())
                                       .numeroCertificat(certificat.getNumeroCertificat())
                                       .registre(regis)
                                       .nomComplet(certificat.getNomComplet())
                                       .dateNaissance(certificat.getDateNaissance())
                                       .lieuNaissance(certificat.getLieuNaissance())
                                       .dateRegistre(certificat.getDateRegistre())
                                       .dateDeces(certificat.getDateDeces())
                                       .lieuDeces(certificat.getLieuDeces())
                                       .NomPere(certificat.getNomPere())
                                       .nomMere(certificat.getNomMere())
                                       .utilisateur(usex)
                                       .build();
      this.certificatDecesRepository.save(certificatdeces);
      XXX= ResponseEntity.ok().body("CERTIFICAT DE DECES CREE N° "+ certificat.getNumeroCertificat());}
    else{
      XXX = ResponseEntity.badRequest().body("CERTIFICAT DE DECES EST DEJA ENREGISTRE");
    }
    return XXX;
   
  }



//Modifier un certificat Deces
  public ResponseEntity<?> updateDeath(DeathDtoRequest certificat, Long id) {
        ResponseEntity TTT =null;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     Death certi = this.certificatDecesRepository.findByIdAndCommune(id,usex.getCommune());

       
   if(certi==null){TTT = ResponseEntity.status(HttpStatusCode.valueOf(403)).body("CERTIFICAT DE DECES INCONNU");}
                      else{
                        certi.setCommune(usex.getCommune());
                        certi.setNumeroCertificat(certificat.getNumeroCertificat());
                        certi.setCommune(usex.getCommune());
                        certi.setNomComplet(certificat.getNomComplet());
                        certi.setDateNaissance(certificat.getDateNaissance());
                        certi.setLieuNaissance(certificat.getLieuNaissance());
                        certi.setDateRegistre(certificat.getDateRegistre());
                        certi.setDateDeces(certificat.getDateDeces());
                        certi.setLieuDeces(certificat.getLieuDeces());
                        certi.setNomPere(certificat.getNomPere());
                        certi.setNomMere(certificat.getNomMere());
                        this.certificatDecesRepository.save(certi);
                        TTT=ResponseEntity.ok().body("CERTIFICAT "+ certi.getNumeroCertificat()+ " A ETE MODIFIEE AVEC SUCCES");
  }
  return TTT;
  }



  //Lire certificat ou les certificats
  public Stream<DeathdtoResponse> readDeath(String num) {
      boolean notEmpty = Strings.isNotEmpty(num);
      Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
      if(notEmpty )
          { Death THOR = this.certificatDecesRepository.findByNumeroCertificat(num);
            if(THOR==null){throw new RuntimeException("CERTIFICAT DE DECES INEXISTANT");}
           //MagicID.magic=THOR.getId();
           return this.certificatDecesRepository.findByNumeroCertificatAndCommune(num, usex.getCommune()).stream()
                   .map(deathMapperDto); }


      return this.certificatDecesRepository.findAllByCommune(usex.getCommune())
              .stream()
              .filter(Objects::nonNull) // filtrer les nulls AVANT le map
              .map(deathMapperDto);


  }


//supprimer certificat deces
  public String deathDeletion() {
    Death DEXA;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  try{
    Death Xtrait = this.certificatDecesRepository.findByEmailAndCommune(MagicID.magic,usex.getCommune());
    DEXA=Xtrait;
  }
   catch(Exception e){throw new RuntimeException("SUPPRESION IMPOSSIBLE-CERTIFICAT DE MARIAGE INTROUVABLE");}
     log.info("XTRAIT :"+DEXA);
     this.certificatDecesRepository.deleteByEmailAndCommune(MagicID.magic, usex.getCommune());
     return "CERTIFICAT DE MARIAGE N° "+DEXA.getNumeroCertificat()+" A ETE SUPPRIME" ;
    

  }

  
    
}