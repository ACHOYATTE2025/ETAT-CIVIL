package com.saasdemo.backend.dto;

import java.sql.Date;

import com.saasdemo.backend.enums.RegimeMariage;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class WeddingDtoRequest {
  private String numeroCertificatMariage;

 
  //information epoux
  private Date dateMariage;
  private String nomEpoux;
  private String professionEpoux;
  private Date dateNaissanceEpoux;
  private String lieuNaissanceEpoux;
  private String domicileEpoux;
  private String nomPere;
  private String nomMere;


  //information epouse
  
  private String nomEpouse;
  private String professionEpouse;
  private Date dateNaissanceEpouse;
  private String lieuNaissanceEpouse;
  private String domicileEpouse;
  private String nomPereEpouse;
  private String nomMereEpouse;

// regime mariage
@Enumerated(value = EnumType.STRING)
private RegimeMariage regimeMariage;
    
}