package com.saasdemo.backend.dto;

import java.sql.Date;

import lombok.Data;
import lombok.Getter;

@Data

public class DeathDtoRequest {

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



}