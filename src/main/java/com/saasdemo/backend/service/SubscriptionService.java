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



    /**
     * Creates a manual or trial subscription for the authenticated user (must be ADMIN).
     * Checks for existing active subscriptions for the commune, disables expired subscriptions,
     * and saves new or updates existing subscription based on the amount.
     */
    public ResponseEntity<SouscriptionResponseDto> createSubscriptionForUser(SubscriptionDTO dto) {
        dtox = dto;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            log.error("User not properly authenticated.");
            throw new RuntimeException("Utilisateur non authentifié correctement.");
        }
        this.admin = admin;

        Utilisateur user = utilisateurRepository.findById(admin.getId())
                .orElseThrow(() -> {
                    log.error("User {} not found.", admin.getUsername());
                    return new RuntimeException(admin.getUsername() + " introuvable");
                });

        if (user.getRole() == null || user.getRole().getLibele() != TypeRole.ADMIN) {
            log.error("Only ADMIN can create a subscription.");
            throw new RuntimeException("Seul un ADMIN peut créer une souscription.");
        }

        // Check if an active subscription already exists for the commune
        boolean exists = subscriptionRepository.existsByCommuneAndActiveTrueAndEndDateAfter(admin.getCommune(), LocalDateTime.now());

        // Retrieve TRIAL subscription if it exists
        Optional<Subscription> susex = subscriptionRepository
                .findByUsersNameAndActiveTrueAndEndDateAfterAndStatus(
                        admin.getUsername(),
                        LocalDateTime.now(),
                        StatutAbonnement.TRIAL
                );

        if (exists && susex.isEmpty()) {
            log.error("An active subscription already exists for this commune.");
            throw new RuntimeException("Une souscription active existe déjà pour cette commune.");
        }

        // Disable all expired subscriptions
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expired = subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateBefore(admin.getUsername(), now);
        expired.forEach(s -> {
            s.setActive(false);
            subscriptionRepository.save(s);
            log.info("Disabled expired subscription: {}", s.getNumero());
        });

        // Determine subscription status and end date by amount
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
            log.error("Invalid amount for subscription: {}", dto.getAmount());
            throw new RuntimeException("Mauvais montant");
        }

        // Create or update subscription
        Subscription subscription;
        if (susex.isPresent() && susex.get().getStatus().equals(StatutAbonnement.TRIAL)) {
            subscription = susex.get();
            subscription.setAmount(dto.getAmount());
            subscription.setStatus(activexx);
            subscription.setEndDate(datexx);
            subscription.setActive(true);
            log.info("Updating TRIAL subscription to ACTIVE for user {}", admin.getUsername());
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
            log.info("Creating new subscription for user {}", admin.getUsername());
        }

        subscriptionRepository.save(subscription);

        // Save the subscription creation operation
        OperationsSaving opsave = OperationsSaving.builder()
                        .name(admin.getUsername())
                        .email(admin.getEmail())
                        .operationDate(Instant.now())
                        .operationNature(TypeOperation.CREATTION_SOUSCRIPTION)
                        .NumeroActe(subscription.getNumero())
                        .build();
        operationSavingRepository.save(opsave);
        log.info("Operation saved: {}", opsave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SouscriptionResponseDto(200, "Souscription éffectuée"));
    }




    // ----------------- Paystack Methods -------------------

    /**
     * Initializes a payment with Paystack API for the authenticated user.
     * Prepares payment details, sends request, and returns the authorization URL for payment.
     */
    public String initializePaystackPayment(SubscriptionDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Utilisateur admin)) {
            log.error("User not properly authenticated.");
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
        body.put("amount", dto.getAmount() * 100); // in kobo for Paystack
        body.put("metadata", Map.of("username", admin.getUsername(), "tenantId", admin.getCommune().getId()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        String authorizationUrl = (String) data.get("authorization_url");
        reference = (String) data.get("reference"); // <-- this is important

        log.info("Initialized Paystack payment for user {}: reference={}, url={}", admin.getUsername(), reference, authorizationUrl);

        return authorizationUrl;
    }


    /**
     * Verifies payment with Paystack API using the stored reference.
     * If successful, creates or activates the subscription for the user.
     * Returns the active subscription object.
     */
    public Subscription verifyPaystackPayment() {

        String url = baseUrl + "/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || !(Boolean) body.get("status")) {
            log.error("Paystack verification failed: {}", body);
            throw new RuntimeException("Vérification échouée : " + body);
        }

        Map<String, Object> data = (Map<String, Object>) body.get("data");
        String paymentStatus = (String) data.get("status");
        Integer amountKobo = (Integer) data.get("amount");
        int amount = amountKobo / 100; // convert to main unit

        if (!"success".equalsIgnoreCase(paymentStatus)) {
            log.error("Payment not successful for reference: {}", reference);
            throw new RuntimeException("Paiement non réussi pour référence : " + reference);
        }

        // Create or activate subscription
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setAmount(amount);
        createSubscriptionForUser(dto);

        // Return the active subscription
        Subscription activeSubscription = subscriptionRepository.findByUsersNameAndActiveTrueAndEndDateAfter(
                admin.getUsername(), LocalDateTime.now()
        ).orElseThrow(() -> {
            log.error("Active subscription not found after payment for user {}", admin.getUsername());
            return new RuntimeException("Souscription non trouvée après paiement");
        });

        log.info("Verified Paystack payment and activated subscription: {}", activeSubscription.getNumero());

        return activeSubscription;
    }


}
