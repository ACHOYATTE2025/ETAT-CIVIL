package com.saasdemo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChatMessage {
  @NotBlank
  private String sender;
  @NotBlank
  @Size(max = 100, message = "Le message ne doit pas dépasser 100 caractères")
  private String value;
  @NotBlank
  private String timestamp;
  
}