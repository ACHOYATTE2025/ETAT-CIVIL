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

/**
 * REST Controller for handling subscription functionalities in ETAT CIVIL application.
 * This controller provides endpoints for initializing subscriptions, redirecting for payment validation,
 * and verifying payments via Paystack integration.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(
  name = "SUBSCRIPTION PAYSTACK REST Api for ETAT CIVIL",
  description="SUBSCRIPTION PAYSTACK REST Api in  ETAT CIVIL APP TO PAY BILL TO SUBSCRIBE"
)
public class SubscriptionController {

  // Service layer for subscription related business logic
  public final SubscriptionService subscriptionService;

  // Stores the payment URL after initialization for later use in redirection
  private ResponseEntity<String> urlvalidation;

  /**
   * Endpoint to initialize a subscription.
   * Only accessible to users with ADMIN role.
   * Returns a payment URL for Paystack.
   *
   * @param dto SubscriptionDTO containing subscription details
   * @return ResponseEntity with payment URL as String
   */
  @Operation(
    summary="REST API to initialize subscription into APP ETAT CIVIL",
    description = "REST API to initialize subscription inside ETAT CIVIL APP"
  )
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/subscription")
  public ResponseEntity<String> createSubscriptioninitialize(@RequestBody SubscriptionDTO dto) {
        log.info("➡️ Request to initialize subscription: {}", dto);
        
        // Call service to initialize Paystack payment and get the payment URL
        String payUrl = subscriptionService.initializePaystackPayment(dto);
        
        // Store payment URL in urlvalidation for future redirect use
        urlvalidation = ResponseEntity.ok(payUrl);
        log.info("✅ Subscription initialized. Payment URL: {}", payUrl);
        return urlvalidation;
  }

  /**
   * Endpoint to redirect to the payment validation URL.
   * Only accessible to users with ADMIN role.
   * Redirects to the URL stored in urlvalidation if available.
   *
   * @return ResponseEntity<Void> with redirection or error status
   */
  @Operation(
    summary = "Redirect  REST API to validate payment into APP ETAT CIVIL",
    description = "Subscription validation"
  )
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/redirect")
  public ResponseEntity<Void> redirectToUrl() {
    try {
        log.info("➡️ Request to redirect to payment URL");
        // Get the payment URL from urlvalidation
        String urlfound = urlvalidation.getBody(); 
        
        // Check if the URL is valid
        if (urlfound == null || urlfound.isEmpty()) {
            log.error("❌ URL found is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 error if URL is invalid
        }

        // Perform the redirection to payment URL
        URI url = URI.create(urlfound);
        log.info("✅ Redirecting to URL: {}", url);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(url)
                .build();
        
    } catch (Exception e) {
        log.error("❌ Error occurred during URL redirection", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 error in case of exception
    }
  }

  /**
   * Endpoint to verify the payment for a subscription.
   * Only accessible to users with ADMIN role.
   * Returns the Subscription entity after verification.
   *
   * @return ResponseEntity with Subscription details
   */
  @Operation(
    summary="REST API to verify subsctiption   into APP ETAT CIVIL",
    description = "REST API to verify subscription  inside ETAT CIVIL App "
  )
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/verify/{reference}")
  public ResponseEntity<Subscription> verifyPayment() {
        log.info("➡️ Request to verify subscription payment");
        // Call service to verify Paystack payment and get Subscription details
        Subscription subscription = subscriptionService.verifyPaystackPayment();
        log.info("✅ Subscription verified: {}", subscription);
        return ResponseEntity.ok(subscription);
  }

}
