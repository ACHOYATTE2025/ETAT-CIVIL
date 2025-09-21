package com.saasdemo.backend.entity;

import java.sql.Date;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "death")
public class Death extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)

  private Long id;

 //l'année du registre
  @ManyToOne
  @JoinColumn(name = "registre_id")
  private Registre registre;

  @NaturalId
  private  String email;

  private String numeroCertificat;
  private Date dateRegistre;
  private Date dateDeces;
  private String nomComplet;
  private String lieuDeces;
  private String lieuNaissance;
  private Date dateNaissance;
  private String profession;
  private String NomPere;
  private String nomMere;
  private Date dateDelivrance;


  
//les communes 
@ManyToOne
@JoinColumn(name = "commune_id")
  private Area commune;

//utilisateur
@ManyToOne
@JoinColumn(name = "utilisateur_id")
private Utilisateur utilisateur;



    
}