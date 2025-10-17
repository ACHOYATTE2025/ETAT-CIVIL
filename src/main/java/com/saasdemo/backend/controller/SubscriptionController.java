package com.saasdemo.backend.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor

@Tag(
  name = "SUBSCRIPTION PAYSTACK REST Api for ETAT CIVIL",
  description="SUBSCRIPTION PAYSTACK REST Api in  ETAT CIVIL APP TO PAY BILL TO SUBSCRIBE"
)
public class SubscriptionController {

  public final SubscriptionService subscriptionService;

   private ResponseEntity<String> urlvalidation;


  

//suscribe to services
 @Operation(
    summary="REST API to initialize subscription into APP ETAT CIVIL",
    description = "REST API to initialize subscription inside ETAT CIVIL APP"
  )
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/subscription")
public ResponseEntity<String> createSubscriptioninitialize(@RequestBody SubscriptionDTO dto) {
        String payUrl = subscriptionService.initializePaystackPayment(dto);
          urlvalidation = ResponseEntity.ok(payUrl);
          return urlvalidation;
}




//subscription checking
 
@Operation(
    summary = "Redirect  REST API to validate payment into APP ETAT CIVIL",
    description = "Subscription validation"
)
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/redirect")
public ResponseEntity<Void> redirectToUrl() {
    try {
        // Récupérer l'URL depuis le service de validation (urlvalidation)
        String urlfound = urlvalidation.getBody(); 
        
        // Vérifier si l'URL est valide
        if (urlfound == null || urlfound.isEmpty()) {
            log.error("URL found is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Retourne une erreur 400 si l'URL est invalide
        }

        // Effectuer la redirection
        URI url = URI.create(urlfound);
        log.info("Redirecting to URL: " + url);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(url)
                .build();
        
    } catch (Exception e) {
        log.error("Error occurred during URL redirection", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retourne une erreur 500 en cas d'exception
    }
}

//verify
 @Operation(
    summary="REST API to verify subsctiption   into APP ETAT CIVIL",
    description = "REST API to verify subscription  inside ETAT CIVIL App "
  )
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/verify/{reference}")
    public ResponseEntity<Subscription> verifyPayment() {
        Subscription subscription = subscriptionService.verifyPaystackPayment();
        return ResponseEntity.ok(subscription);
    }

    
} 