package com.saasdemo.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasdemo.backend.entity.PaymentLog;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Tenant;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.SubscriptionRepository;
import com.saasdemo.backend.repository.TenantRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaystackService  {

 private final com.saasdemo.backend.repository.PaymentLogRepository PaymentLogRepository;
 private final ObjectMapper objectMapper = new ObjectMapper();
 private final RestTemplate restTemplate = new RestTemplate();
 private final UtilisateurRepository utilisateurRepository;
 private final TenantService tenantService;
 private SubscriptionRepository subscriptionRepository;
 private TenantRepository tenantRepository;


    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    @Value("${paystack.callback.url}")
    private String callbackUrl;
   


    //initier un paiement avec paystack
    public String initializeTransaction(String email, int amountKobo) throws Exception {
        String url = "https://api.paystack.co/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("amount", amountKobo); // montant en Kobo (ex: 10000 = 100.00 NGN ou 1000 = 10 CAD)
        body.put("callback_url", callbackUrl);

        //enregistrer les logs de paiment
        Optional<Utilisateur> userX= this.utilisateurRepository.findByEmail(email);
        if(userX.isEmpty()){throw new RuntimeException("ADMIN NOT FOUND");}else{
            PaymentLog payeX = PaymentLog.builder()
            .email(userX.get().getEmail())
            .amount(amountKobo)
            .commune(userX.get().getCommune())
            .status("en Traitement")
            .paidAt(Instant.now())
            .build();
            this.PaymentLogRepository.save(payeX);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
          Map<String,Object> data = (Map<String, Object>) response.getBody().get("data");
          return (String) data.get("authorization_url");
        } else{
            throw new RuntimeException("Erreur Paystack : " + response.getBody());
        }
    }


    //verification paiement
    public JsonNode verifyTransaction(String reference) throws Exception {
        String url = "https://api.paystack.co/transaction/verify/" + reference;
    
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    
        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.getBody());
        } else {
            throw new RuntimeException("Échec de la vérification de la transaction : " + response.getStatusCode());
        }

  }


    // Calcul du cryptage pour la verification de la signature du webhook
    public String computeHmacSHA512(String data, String secret) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(keySpec);
            byte[] hashBytes = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hashBytes); // Apache Commons Codec
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul HMAC SHA512", e);
        }
    }

//traitement de chargesuccess
    public void handleChargeSuccess(Map<String, Object> data) {
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
      String tenantId = (String) metadata.get("tenant_id");

        String reference = (String) data.get("reference");
        Integer amount = (Integer) data.get("amount");
       String plan = (String) data.get("plan");
      

        Map<String, Object> customer = (Map<String, Object>) data.get("customer");
        String email = (String) customer.get("email");

        System.out.println("✅ charge.success pour " + email + " | Référence : " + reference + " | Montant : " + amount);
        // Enregistre la transaction ou l'abonnement selon le besoin
        Utilisateur us = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Subscription subscription = Subscription.builder()
                                    .id(tenantId)
                                    .status("ACTIVE")
                                    .commune(us.getCommune())
                                    .Reference(reference)
                                    .planCode(plan)
                                    .amount(amount)
                                    .endDate(LocalDateTime.now().plusYears(1))
                                    .createdAt(LocalDateTime.now())
                                    .build();
        this.subscriptionRepository.save(subscription);
      
        //Activer l'abonnement du TenantId
        Tenant tenant = tenantRepository.findById(tenantId)
          .orElseThrow(() -> new RuntimeException("Tenant non trouvé : " + tenantId));
      tenant.setId(tenantId);
      tenant.setName(tenant.getName());
      tenant.setActive(true);
      tenant.setAbonnementStatut("ACTIVE");
      tenant.setAbonnementExpireLe(LocalDateTime.now().plusMonths(12)); // abonnement de 12 mois
      tenantRepository.save(tenant);
                    
    }


    //traitement de invoice payment
    public void handleInvoicePaymentSucceeded(Map<String, Object> data) {
        Map<String, Object> subscription = (Map<String, Object>) data.get("subscription");
        Map<String, Object> plan = (Map<String, Object>) subscription.get("plan");

        String planCode = (String) plan.get("plan_code");
        String email = (String) ((Map<String, Object>) subscription.get("customer")).get("email");
        String reference = (String) data.get("reference");
        Integer amount = (Integer) data.get("amount");
        
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
        String tenantId = (String) metadata.get("tenant_id");
        

        System.out.println("✅ invoice.payment_succeeded | Email : " + email + " | Plan : " + planCode);
        // Mettre à jour l’abonnement récurrent mensuel 

        Utilisateur us = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Subscription subscriptionx = Subscription.builder()
                                    .id(tenantId)
                                    .status("ACTIVE")
                                    .commune(us.getCommune())
                                    .Reference(reference)
                                    .planCode(planCode)
                                    .amount(amount)
                                    .endDate(LocalDateTime.now().plusYears(1))
                                    .createdAt(LocalDateTime.now())
                                    .build();
        this.subscriptionRepository.save(subscriptionx);
      

           //Activer l'abonnement du TenantId
           Tenant tenant = tenantRepository.findById(tenantId)
           .orElseThrow(() -> new RuntimeException("Tenant non trouvé : " + tenantId));
       tenant.setId(tenantId);
       tenant.setName(tenant.getName());
       tenant.setActive(true);
       tenant.setAbonnementStatut("ACTIVE");
       tenant.setAbonnementExpireLe(LocalDateTime.now().plusMonths(1)); // abonnement de 01 mois
       tenantRepository.save(tenant);
                     

    }


   
}


  
