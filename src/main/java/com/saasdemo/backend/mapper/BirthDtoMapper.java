package com.saasdemo.backend.mapper;


import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.entity.Birth;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.function.Function;

@Component
public class BirthDtoMapper implements Function<Birth, BirthDtoResponse> {
    @Override
    public BirthDtoResponse apply(Birth birth) {
        return new BirthDtoResponse(birth.getNumeroExtrait(),birth.getDateDelivrance() ,birth.getLieuDelivrance(),
                birth.getNomComplet(), birth.getDateNaissance(), birth.getLieuNaissance(),
                birth.getNomPere(), birth.getProfessionPere(), birth.getDomicilePere(),
                birth.getNationalitePere(), birth.getNomMere(), birth.getProfessionMere(),
                birth.getDomicileMere(), birth.getNationaliteMere(), birth.getMarie(),
                birth.getMarieAvec(), birth.getNumeroDecisionDM(), birth.getDissolutionMariage(),
                birth.getDeces());
    }
}


