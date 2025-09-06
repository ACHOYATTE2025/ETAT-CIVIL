package com.saasdemo.backend.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.saasdemo.backend.dto.SouscriptionResponseDto;
import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.OperationsSaving;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.StatutAbonnement;
import com.saasdemo.backend.enums.TypeOperation;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.repository.OperationSavingRepository;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class SubscriptionService {

  private final UtilisateurRepository utilisateurRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final OperationSavingRepository operationSavingRepository;

  
  
    @Value("${paystack.secret.key}")
    private String secretKey;

    @Value("${paystack.base.url}")
    private String baseUrl;

    


    public SubscriptionService(UtilisateurRepository utilisateurRepository, SubscriptionRepository subscriptionRepository,
                               PdfService pdfService, OperationSavingRepository operationSavingRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.operationSavingRepository = operationSavingRepository;
    }

  private String reference;
  private Utilisateur admin=null;
  private SubscriptionDTO dtox;
  private String Validitex;



    // Méthode existante pour créer une souscription TRIAL / manuelle
    public ResponseEntity<SouscriptionResponseDto> createSubscriptionForUser(SubscriptionDTO dto) {
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
        if (susex.isPresent() && susex.get().getStatus().equals(StatutAbonnement.TRIAL)) {
            subscription = susex.get();
            subscription.setAmount(dto.getAmount());
            subscription.setStatus(activexx);
            subscription.setEndDate(datexx);
            subscription.setActive(true);
        } else {
            subscription = Subscription.builder()
                    .numero(UUID.randomUUID().toString())
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

        
        OperationsSaving opsave = OperationsSaving.builder()
                        .name(admin.getUsername())
                        .email(admin.getEmail())
                        .operationDate(Instant.now())
                        .operationNature(TypeOperation.CREATTION_SOUSCRIPTION)
                        .NumeroActe(subscription.getNumero())
                        .build();
        operationSavingRepository.save(opsave);
        log.info("operation effectuée :"+opsave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SouscriptionResponseDto(200, "Souscription éffectuée"));
    }




    // ----------------- Méthodes Paystack -------------------

    public String initializePaystackPayment(SubscriptionDTO dto) {
               Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            throw new RuntimeException("Utilisateur non authentifié correctement.");
        }
        this.admin = admin;

       //dto.setAmount(fcfaToKobo(dto.getAmount()));

        String url = baseUrl + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("email", admin.getEmail());
        body.put("amount", dto.getAmount()*100); // en kobo pour Paystack
        body.put("metadata", Map.of("username", admin.getUsername(), "tenantId", admin.getCommune().getId()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        String authorizationUrl = (String) data.get("authorization_url");
        reference = (String) data.get("reference"); // <-- ceci est important

        return authorizationUrl;
    }





public Subscription verifyPaystackPayment() {

     String url = baseUrl + "/transaction/verify/" + reference;

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(secretKey);

    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

    Map<String, Object> body = response.getBody();
    if (body == null || !(Boolean) body.get("status")) {
        throw new RuntimeException("Vérification échouée : " + body);
    }

    Map<String, Object> data = (Map<String, Object>) body.get("data");
    String paymentStatus = (String) data.get("status");
    Integer amountKobo = (Integer) data.get("amount");
    int amount = amountKobo / 100; // convertir en unité principale

    if (!"success".equalsIgnoreCase(paymentStatus)) {
        throw new RuntimeException("Paiement non réussi pour référence : " + reference);
    }

    // Créer ou activer la souscription
    SubscriptionDTO dto = new SubscriptionDTO();
    dto.setAmount(amount);
    createSubscriptionForUser(dto);

    // Retourner la souscription active
    return subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateAfter(
            admin.getUsername(), LocalDateTime.now()
    ).orElseThrow(() -> new RuntimeException("Souscription non trouvée après paiement"));
}


}