package com.saasdemo.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.config.MagicID;
import com.saasdemo.backend.dto.CertificatDecesDto;
import com.saasdemo.backend.entity.CertificatDeces;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.CertificatDecesRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificatDecesService {
  private CertificatDecesRepository certificatDecesRepository;
  private RegistreRepository registreRepository;
  private ResponseEntity XXX;



  
  /*==============================================*/
  /*       Volet Certificat de mariage            */
  /*==============================================*/
  
   
  
  //creation certificat deces
  public ResponseEntity<?> creerCertificatDeces(CertificatDecesDto certificat) {
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //configurer le tenant
    TenantContext.setCurrentTenantId(usex.getCommune().getId());
    Optional<List<CertificatDeces>> certi = this.certificatDecesRepository.findByNumeroCertificatAndCommune(certificat.getNumeroCertificat(),usex.getCommune());

    if(certi.get().size()==0){

      // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);
    
      CertificatDeces certificatdeces= CertificatDeces.builder()
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
                                       .build();
      this.certificatDecesRepository.save(certificatdeces);
      XXX= ResponseEntity.ok().body("CERTIFICAT DE DECES CREE N° "+ certificat.getNumeroCertificat());}
    else{
      XXX = ResponseEntity.badRequest().body("EXTRAIT DE DECES EST DEJA ENREGISTRE");
    }
    return XXX;
   
  }



//Modifier un certificat Deces
  public ResponseEntity<?> modifierCertificatDeces(CertificatDecesDto certificat, Long id) {
        ResponseEntity TTT =null;
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     //configurer le tenant
     TenantContext.setCurrentTenantId(usex.getCommune().getId());
     CertificatDeces certi = this.certificatDecesRepository.findByIdAndCommune(id,usex.getCommune());

       
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
  public Optional<List<CertificatDeces>> lireCertificatDeces(String num) {
      boolean notEmpty = Strings.isNotEmpty(num);
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  
  if(notEmpty )
      { CertificatDeces THOR = this.certificatDecesRepository.findByNumeroCertificat(num);
        if(THOR==null){throw new RuntimeException("CERTIFICAT DE DECES INEXISTANT");}
       MagicID.magic=THOR.getId();
       return this.certificatDecesRepository.findALLByNumeroCertificatAndCommune(num, usex.getCommune());}
    return this.certificatDecesRepository.findAllByCommune(usex.getCommune());

  }


//supprimer certificat deces
  public String supprimerCertificatDeces() {
    CertificatDeces DEXA;
  Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  TenantContext.setCurrentTenantId(usex.getId());
  try{
    CertificatDeces Xtrait = this.certificatDecesRepository.findByIdAndCommune(MagicID.magic,usex.getCommune());
    DEXA=Xtrait;
  }
   catch(Exception e){throw new RuntimeException("SUPPRESION IMPOSSIBLE-CERTIFICAT DE MARIAGE INTROUVABLE");}
     log.info("XTRAIT :"+DEXA);
     this.certificatDecesRepository.deleteByIdAndCommune(MagicID.magic, usex.getCommune());
     return "CERTIFICAT DE MARIAGE N° "+DEXA.getNumeroCertificat()+" A ETE SUPPRIME" ;
    

  }

  
    
}