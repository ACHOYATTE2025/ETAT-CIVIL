package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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



  
}