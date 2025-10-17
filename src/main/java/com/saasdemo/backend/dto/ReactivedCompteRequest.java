package com.saasdemo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReactivedCompteRequest {
  @NotBlank
  private String email;
    
}