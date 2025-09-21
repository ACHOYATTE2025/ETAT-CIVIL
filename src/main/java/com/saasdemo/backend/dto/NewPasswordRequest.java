package com.saasdemo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPasswordRequest {
  @NotBlank
  private String email;

  @NotBlank
  private String code;

  @NotBlank
  private String password;


    
}