package com.saasdemo.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.saasdemo.backend.service.PaystackService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class PaystackController {

   private final PaystackService paystackService;

    /**
     * Endpoint permettant à un utilisateur (typiquement un ADMIN) d'initialiser une transaction Paystack.
     * Le montant est passé dans la requête (en Kobo, par exemple 10000 correspond à 100.00 NGN – adapte selon ta devise).
     */
    @PostMapping("/startPaystack")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> startPaystackTransaction(@RequestParam String email,
                                                                        @RequestParam int amountKobo) {
        try {
            String authorizationUrl = paystackService.initializeTransaction(email, amountKobo);
            return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                                 .body(Map.of("error", "Erreur lors de l'initialisation de la transaction: " + e.getMessage()));
        }
    }

    /*
     Explications :

    * L’endpoint /api/payment/start-paystack est accessible via une méthode POST.

    * Il est protégé par @PreAuthorize("hasRole('ADMIN')"), ce qui signifie que seul un utilisateur ayant le rôle ADMIN peut l’appeler (assure-toi d’avoir configuré le système de rôles et l’authentification correctement).

    Les paramètres email et amountKobo sont attendus en paramètre de la requête HTTP (tu peux également les passer dans le corps de la requête selon tes préférences).

   * En cas de succès, le service renvoie l’URL d’autorisation générée par Paystack, que tu pourras utiliser côté front pour rediriger l’utilisateur vers la page de paiement Paystack.
     */


// verification du callBack après paiement
@GetMapping("/paystackCallback") 
public ResponseEntity<String> paystackCallback( @RequestParam("reference") String reference ){
    try { JsonNode verification = paystackService.verifyTransaction(reference); 
        String status = verification.path("data").path("status").asText();
        if ("success".equals(status)) {
        // ✅ Paiement confirmé – active l’abonnement ici
        return ResponseEntity.ok("Paiement confirmé. Référence : " + reference);
    } else {
        // ❌ Paiement non validé
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Paiement non confirmé. Statut : " + status);
    }
    } catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Erreur lors de la vérification : " + e.getMessage());
    }
        
    }



   //mise en place des webhooks
   @PostMapping(path="/paystackWebhooks")
    public ResponseEntity<String> handlePaystackWebhook(@RequestBody Map<String, Object> payload,
                                                        @RequestHeader("x-paystack-signature") String signature) {
        boolean isValid = paystackService.verifyWebhookSignature(payload, signature);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        String event = (String) payload.get("event");

        if ("charge.success".equals(event)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String reference = (String) data.get("reference");
            Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
            String tenantId = (String) metadata.get("tenant_id");

           this.paystackService.handleSuccessfulPayment(reference, tenantId);
        }

        return ResponseEntity.ok("Webhook received");
    }


    }
    
    
