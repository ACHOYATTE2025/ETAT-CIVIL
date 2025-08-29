package com.saasdemo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.SouscriptionResponseDto;
import com.saasdemo.backend.dto.SubscriptionDTO;
import com.saasdemo.backend.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

@Tag(
  name = "SUBSCRIPTION   REST Api for ETAT CIVIL",
  description="SUBSCRIPTION  REST Api in  ETAT CIVIL APP to CREATE,READ,UPDATE,DELETE  details"
)
public class SubscriptionController {

  public final SubscriptionService subscriptionService;




  

//suscribe to services
 @Operation(
    summary="REST API to subscribe  into APP ETAT CIVIL",
    description = "REST API to subscribe inside ETAT CIVIL APP"
  )
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/subscription")
public ResponseEntity<SouscriptionResponseDto> createSubscription(@RequestBody SubscriptionDTO dto) {
    return this.subscriptionService.createSubscriptionForUser(dto);}


    
} 