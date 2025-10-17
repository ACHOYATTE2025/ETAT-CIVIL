package com.saasdemo.backend.entity;

import java.sql.Date;
import java.time.LocalDate;

import org.hibernate.annotations.NaturalId;

import com.saasdemo.backend.enums.RegimeMariage;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "wedding")
public class Wedding extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)

  private Long id;
  
  private String numeroCertificatMariage;

   @NaturalId
  private  String email;

  //l'année du registre
  @ManyToOne
  @JoinColumn(name = "registre_id")
  private Registre registre;

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

  // date de delivrance mariage
  @Builder.Default
  private LocalDate dateDelivranceDocument =LocalDate.now();


//les communes 
@ManyToOne
@JoinColumn(name = "commune_id")
  private Area commune;

//utilisateur
@ManyToOne
@JoinColumn(name = "utilisateur_id")
private Utilisateur utilisateur;


}