package com.saasdemo.backend.dto;

import java.sql.Date;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BirthDtoResponse {

  public BirthDtoResponse(String numeroExtrait2, String lieuDelivrance2, LocalDate dateDelivrance2, String nomComplet2,
      String lieuNaissance2, LocalDate dateNaissance2, String nomPere2, String professionPere2, String domicilePere2,
      String nationalitePere2, String nomMere2, String professionMere2, String domicileMere2, String nationaliteMere2,
      String marie2, String marieAvec2, String numeroDecisionDM2, LocalDate dissolutionMariage2, LocalDate deces2) {
    //TODO Auto-generated constructor stub
  }
  private String NumeroExtrait;
  private  String lieuDelivrance;
  private  Date dateDelivrance;


//information naissance
  private String nomComplet;
  private String lieuNaissance;
  private Date dateNaissance;
  

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
  private Date dissolutionMariage;
  private Date deces;
  
    
}