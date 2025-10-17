package com.saasdemo.backend.dto;

import java.time.LocalDate;

import com.saasdemo.backend.enums.RegimeMatrimoniale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(name = "Birth",
        description = "Schema to hold birth information"
)
public class BirthDtoResponse {


  private String NumeroExtrait;
  private  String lieuDelivrance;
  private  LocalDate dateDelivrance;


//information naissance
  private String nomComplet;
  private String lieuNaissance;
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
  @Enumerated(EnumType.STRING)
  private RegimeMatrimoniale marie;
  private String marieAvec;
  private String numeroDecisionDM;
  private LocalDate dissolutionMariage;
  private LocalDate deces;
  
    
}