package com.saasdemo.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.CertificatDto;
import com.saasdemo.backend.entity.CertificatMariage;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.CertificatMariageRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificatMariageService {
  private final CertificatMariageRepository certificatMariageRepository;
  private final RegistreRepository registreRepository;
  ResponseEntity XXX;




  //creation certificat
  public ResponseEntity<?> creerCertificat(CertificatDto certificat) {
    Utilisateur usex = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //configurer le tenant
    TenantContext.setCurrentTenantId(usex.getCommune().getId());
    Optional<List<CertificatMariage>> certi = this.certificatMariageRepository.findByNumeroCertificatMariageAndCommune(certificat.getNumeroCertificatMariage(),usex.getCommune());

    if(certi.get().size()==0){

      // configurer le registre
          Registre regis = Registre.builder()
                          .registreAnnee(String.valueOf(LocalDate.now().getYear()))
                          .build();
          this.registreRepository.save(regis);
    
      CertificatMariage certificamariage= CertificatMariage.builder()
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
                                        .nomMere(certificat.getNomMere())
                                        .nomEpoux(certificat.getNomEpoux())
                                        .nomPereEpouse(certificat.getNomPereEpouse())
                                        .nomMereEpouse(certificat.getNomMereEpouse())
                                        .professionEpouse(certificat.getProfessionEpouse())
                                        .professionEpoux(certificat.getNomEpoux())
                                        .regimeMariage(certificat.getRegimeMariage())
                                        .registre(regis)
                                        .build();
      this.certificatMariageRepository.save(certificamariage);
      XXX= ResponseEntity.ok().body("CERTIFICAT DE MARIAGE CREE N° "+ certificat.getNumeroCertificatMariage());}
    else{
      XXX = ResponseEntity.badRequest().body("EXTRAIT DE NAISSANCE EST DEJA ENREGISTRE");
    }
    return XXX; }


}