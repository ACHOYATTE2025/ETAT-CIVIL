package com.saasdemo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginAdminRequest {
  @NotBlank
  private String email;

  @NotBlank
  private String password;
    
}