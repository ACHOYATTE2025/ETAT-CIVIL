package com.saasdemo.backend.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "extraitNaissance")
public class ExtraitNaissance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;




  private  String numeroExtrait;
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
  



 /* @Lob
  Byte[] carnet;
  
  @Lob
  Byte[] cni1;

  @Lob
  Byte[] cni2;*/

@ManyToOne
@JoinColumn(name = "utilisateur_id")
private Utilisateur utilisateur;

@ManyToOne
@JoinColumn(name = "commune_id")
  private Commune commune;

//données extrait
 
@ManyToOne
@JoinColumn(name = "registre_id")
private Registre registre;


}