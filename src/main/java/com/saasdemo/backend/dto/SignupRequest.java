package com.saasdemo.backend.dto;

import com.saasdemo.backend.entity.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {

  @NotBlank
  private String username;

  @NotBlank
  private String email;

  @NotBlank
  private String password;

  @NotBlank
  private Role role;

  @NotBlank
  private String namecommune;


    
}