package com.saasdemo.backend.dto;

import com.saasdemo.backend.enums.StatutAbonnement;

import lombok.Data;

@Data
public class SubscriptionDTO {
   private double amount;
   private StatutAbonnement status;
    
}