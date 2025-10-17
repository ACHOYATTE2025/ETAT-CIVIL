package com.saasdemo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
  @NotBlank
  private String fullName;

  @Email
  private String email;

  @NotBlank
  private String password;
    
}