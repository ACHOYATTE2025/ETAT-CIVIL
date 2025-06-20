package com.saasdemo.backend.dto;

import java.util.Date;

public record BirthDtoResponse(




          String lieuDelivrance,
          Date dateDelivrance,

          String numeroExtrait,

    //information naissance
          String nomComplet,
          Date dateNaissance,
          String lieuNaissance,

    //information Pere
          String nomPere,
          String professionPere,
          String domicilePere,
          String nationalitePere,

    //information Mere
          String nomMere,
          String professionMere,
          String domicileMere,
          String nationaliteMere,

        //information sur LES MENTIONS
          String marie,
          String marieAvec,
          String numeroDecisionDM,
          Date dissolutionMariage,
          Date deces
) {
}
