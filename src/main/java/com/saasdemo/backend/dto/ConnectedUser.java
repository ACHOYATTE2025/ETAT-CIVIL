package com.saasdemo.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ConnectedUser {
  private String username;
  private String tenantId;

    
}