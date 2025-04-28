package com.saasdemo.backend.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ExtraitDto {
  private  String numeroExtrait;
  private  Date dateRegistre;
  private  String lieuDelivrance;
  private  Date dateDelivrance;


//information naissance
  private String nomComplet;
  private Date dateNaissance;
  private String lieuNaissance;

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