package com.saasdemo.backend.entity;

import java.time.LocalDate;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saasdemo.backend.enums.RegimeMatrimoniale;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "birth")
public class Birth extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private  String numeroExtrait;

  @NaturalId
  @NotNull
  @Email(message = "Invalid email format")
  private  String email;
  
  private  String lieuDelivrance;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private  LocalDate dateDelivrance;


//information naissance
  private String nomComplet;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateNaissance;

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
  @Enumerated(EnumType.STRING)
  private RegimeMatrimoniale marie;

  private String marieAvec;
  private String numeroDecisionDM;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dissolutionMariage;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate deces;
  

//utilisateur
@ManyToOne
@JoinColumn(name = "utilisateur_id")
private Utilisateur utilisateur;

@ManyToOne
@JoinColumn(name = "commune_id")
  private Area commune;

//données extrait
 @ManyToOne
 @JoinColumn(name = "registre_id")
 private Registre registre;


}