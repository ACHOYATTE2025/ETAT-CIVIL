package com.saasdemo.backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BirthDtoRequest {
  
  private String email;
  private  String lieuDelivrance;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private  LocalDate dateDelivrance;


//information naissance
  private String nomComplet;
  private String lieuNaissance;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateNaissance;
  

//information Pere
  private String nomPere;
  private String professionPere;
  private String domicilePere;
  private String nationalitePere;

//information Mere
  private String nomMere;
  private String professionMere;
  private String domicileMere;
  private String nationaliteMere;

  //information sur LES MENTIONS
  private String marie;
  private String marieAvec;
  private String numeroDecisionDM;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dissolutionMariage;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate deces;
  
    
}