package com.saasdemo.backend.dto;
import java.util.Date;



public record DeathdtoResponse(
         Long id,
         String numeroCertificat,
         Date dateRegistre,
         Date dateDeces,
         String nomComplet,
         String lieuDeces,
         String lieuNaissance,
         Date dateNaissance,
         String profession,
         String NomPere,
         String nomMere,
         Date dateDelivrance

) {
}
