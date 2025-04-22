package com.saasdemo.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasdemo.backend.entity.PaymentLog;
import com.saasdemo.backend.entity.Utilisateur;
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
            .id(userX.get().getId())
            .email(userX.get().getEmail())
            .amount(amountKobo)
            .commune(userX.get().getCommune())
            .status("en Traitement")
            .paidAt(Instant.now())
            .build();
            this.PaymentLogRepository.save(payeX);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.path("data").path("authorization_url").asText();
        } else {
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

// Verification de la signature du webhook
public boolean verifyWebhookSignature(Map<String, Object> payload, String signature) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(payload);

            SecretKeySpec keySpec = new SecretKeySpec(paystackSecretKey.getBytes(), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(json.getBytes(StandardCharsets.UTF_8));

            String expectedSignature = bytesToHex(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }


    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void handleSuccessfulPayment(String reference, String tenantId) {
        // ici tu peux activer l'abonnement dans la base
        this.tenantService.activateSubscription(tenantId);
    }
}


  
