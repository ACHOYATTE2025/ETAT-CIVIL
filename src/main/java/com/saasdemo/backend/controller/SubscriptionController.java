package com.saasdemo.backend.controller;

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

@RestController
@RequiredArgsConstructor

@Tag(
  name = "SUBSCRIPTION PAYSTACK REST Api for ETAT CIVIL",
  description="SUBSCRIPTION PAYSTACK REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
public class SubscriptionController {

  public final SubscriptionService subscriptionService;




  

//suscribe to services
 @Operation(
    summary="PAYSTACK REST API to   into APP ETAT CIVIL",
    description = "PAYSTACK REST API to  inside ETAT CIVIL APP"
  )
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/subscription")
public ResponseEntity<String> createSubscriptioninitialize(@RequestBody SubscriptionDTO dto) {
          String payUrl = subscriptionService.initializePaystackPayment(dto);
        return ResponseEntity.ok(payUrl);
}
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/verify/{reference}")
    public ResponseEntity<Subscription> verifyPayment() {
        Subscription subscription = subscriptionService.verifyPaystackPayment();
        return ResponseEntity.ok(subscription);
    }

    
} 