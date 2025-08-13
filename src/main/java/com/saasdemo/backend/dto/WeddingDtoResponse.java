package com.saasdemo.backend.dto;

import java.sql.Date;

public record WeddingDtoResponse(
        Long Id,
        String numeroCertificatMariage,


        //information epoux
        Date dateMariage,
        String nomEpoux,
        String professionEpoux,
        Date dateNaissanceEpoux,
        String lieuNaissanceEpoux,
        String domicileEpoux,
        String nomPere,
        String nomMere,


        //information epouse

        String nomEpouse,
        String professionEpouse,
        Date dateNaissanceEpouse,
        String lieuNaissanceEpouse,
        String domicileEpouse,
        String nomPereEpouse,
        String nomMereEpouse,
        String Regime) {
}
