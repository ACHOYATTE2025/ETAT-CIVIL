package com.saasdemo.backend.mapper;

import com.saasdemo.backend.dto.WeddingDtoResponse;

import com.saasdemo.backend.entity.Wedding;

import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public class WeddingDtoMapper implements Function<Wedding, WeddingDtoResponse> {
    @Override
    public WeddingDtoResponse apply(Wedding wedding) {
        return  new WeddingDtoResponse(wedding.getId(), wedding.getNumeroCertificatMariage(),
                wedding.getDateMariage(), wedding.getNomEpoux(), wedding.getProfessionEpoux(),
                wedding.getDateNaissanceEpoux(), wedding.getLieuNaissanceEpoux(), wedding.getDomicileEpoux(),
                wedding.getNomPere(), wedding.getNomMere(), wedding.getNomEpouse(), wedding.getProfessionEpouse(),
                wedding.getDateNaissanceEpouse(), wedding.getLieuNaissanceEpouse(), wedding.getDomicileEpouse(),
                wedding.getNomPereEpouse(), wedding.getNomMereEpouse(), wedding.getRegimeMariage().name()
                );
    }
}
