package com.saasdemo.backend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.SouscriptionResponseDto;
import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class SubscriptionService {

  private final UtilisateurRepository utilisateurRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final PdfService pdfService;

  private Utilisateur admin=null;
  private SubscriptionDTO dtox;
  private String Validitex;

  public SubscriptionService(UtilisateurRepository utilisateurRepository, SubscriptionRepository subscriptionRepository,
  PdfService pdfService){
    this.utilisateurRepository = utilisateurRepository;
    this.subscriptionRepository =subscriptionRepository;
    this.pdfService = pdfService;
  }

  
  public  ResponseEntity<SouscriptionResponseDto> createSubscriptionForUser( SubscriptionDTO dto) {
    dtox = dto;
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
if (!(principal instanceof Utilisateur admin)) {
    throw new RuntimeException("Utilisateur non authentifié correctement.");
}
this.admin = admin;

Utilisateur user = utilisateurRepository.findById(admin.getId())
        .orElseThrow(() -> new RuntimeException(admin.getUsername() + " introuvable"));

if (user.getRole() == null || user.getRole().getLibele() != TypeRole.ADMIN) {
    throw new RuntimeException("Seul un ADMIN peut créer une souscription.");
}

// Vérifier si une subscription active existe pour cette commune
boolean exists = subscriptionRepository.existsByCommuneAndActiveTrueAndEndDateAfter(admin.getCommune(), LocalDateTime.now());

// Récupérer subscription TRIAL si elle existe
Optional<Subscription> susex = subscriptionRepository
        .findByUsersNameAndActiveTrueAndEndDateAfterAndStatus(
                admin.getUsername(),
                LocalDateTime.now(),
                StatutAbonnement.TRIAL
        );

if (exists && susex.isEmpty()) {
    throw new RuntimeException("Une souscription active existe déjà pour cette commune.");
}

// Désactiver toutes les souscriptions expirées
LocalDateTime now = LocalDateTime.now();
List<Subscription> expired = subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateBefore(admin.getUsername(), now);
expired.forEach(s -> {
    s.setActive(false);
    subscriptionRepository.save(s);
});

// Déterminer status et date de fin selon le montant
StatutAbonnement activexx;
LocalDateTime datexx;

if (dto.getAmount() == 1000000) {
    activexx = StatutAbonnement.ACTIVE;
    Validitex = "Un (01) An";
    datexx = now.plusYears(1);
} else if (dto.getAmount() == 100000) {
    activexx = StatutAbonnement.ACTIVE;
    datexx = now.plusMonths(1);
    Validitex = "Un (01) Mois";
} else if (dto.getAmount() == 0) {
    activexx = StatutAbonnement.TRIAL;
    datexx = now.plusWeeks(1);
    Validitex = "Une (01) Semaine";
} else {
    throw new RuntimeException("Mauvais montant");
}

// Créer ou mettre à jour
Subscription subscription;
if (susex.isPresent()) {
    subscription = susex.get();
    subscription.setAmount(dto.getAmount());
    subscription.setStatus(activexx);
    subscription.setEndDate(datexx);
    subscription.setActive(true);
} else {
    subscription = Subscription.builder()
            .status(activexx)
            .active(true)
            .amount(dto.getAmount())
            .created(now)
            .commune(admin.getCommune())
            .endDate(datexx)
            .usersName(admin.getUsername())
            .email(admin.getEmail())
            .role(user.getRole())
            .build();
}

subscriptionRepository.save(subscription);
return ResponseEntity 
.status(HttpStatus.CREATED) 
.body(new SouscriptionResponseDto(200, "Souscription éffectuée"));
    
   }


    public  ByteArrayInputStream  generateSubscriptionTicketPdf()  throws IOException {
         // Formatter pour avoir yyyy-MM-dd HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedDate = admin.getSubscription().getEndDate().format(formatter);

         //generer le reçu Pdf de la souscription
               ByteArrayInputStream pdfStream = this.pdfService.generateSubscriptionPdf(admin.getUsername(),admin.getCommune().getNameCommune(),
               dtox.getAmount(),  formattedDate.toString());
            return pdfStream;
    }
}