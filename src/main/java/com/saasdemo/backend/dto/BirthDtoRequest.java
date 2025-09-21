package com.saasdemo.backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saasdemo.backend.enums.RegimeMatrimoniale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "Birth",
        description = "Schema to request information"
)
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BirthDtoRequest {
  

  
   @Schema(
            description = "email of User"
    )
  @Email(message = "Invalid email format")
  private String email;

  
   @Schema(
            description = "Birth's place of birth certificate delivred"
    )  
  private  String lieuDelivrance;


  
   @Schema(
            description = "Birth's date of User"
    )
  @JsonFormat(pattern = "yyyy-MM-dd")
  private  LocalDate dateDelivrance;


//information naissance

   @Schema(
            description = "fullname of User"
    )
  private String nomComplet;

  @Schema(
            description = "birth's place of User"
    )
  private String lieuNaissance;

@Schema(
            description = "birth date of User"
    )
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateNaissance;
  

//information Pere
@Schema(
            description = "father's user name "
    )
  private String nomPere;

  @Schema(
            description = "father's job of User"
    )
  private String professionPere;

  @Schema(
            description = "user's father living place"
    )
  private String domicilePere;

   @Schema(
            description = "user's father nationality"
    )
  private String nationalitePere;

//information Mere
@Schema(
            description = "user's mother name"
    )
  private String nomMere;

  @Schema(
            description = "user's mother job"
    )
  private String professionMere;

  @Schema(
            description = "user's mother living place"
    )
  private String domicileMere;

  
  @Schema(
            description = "user's mother nationality"
    )
  private String nationaliteMere;

  //information sur LES MENTIONS
   @Schema(
            description = "user's wedding date"
    )
  @Enumerated(EnumType.STRING)
  private RegimeMatrimoniale marie;

  @Schema(
            description = "user's wedding partener"
    )
  private String marieAvec;

  @Schema(
            description = "user's wedding reference number"
    )
  private String numeroDecisionDM;

  @Schema(
            description = "user's wedding breaking date"
    )

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dissolutionMariage;


  @Schema(
            description = "user's die date"
    )
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate deces;
  
    
}