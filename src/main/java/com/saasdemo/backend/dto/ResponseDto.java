package com.saasdemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
  

  public ResponseDto() {
    //TODO Auto-generated constructor stub
  }

  private int statusCode;

  private String statusMsg;

    
}