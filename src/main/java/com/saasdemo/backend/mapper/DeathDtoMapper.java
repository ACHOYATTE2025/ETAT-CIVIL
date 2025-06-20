package com.saasdemo.backend.mapper;

import com.saasdemo.backend.dto.DeathdtoResponse;
import com.saasdemo.backend.entity.Death;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DeathDtoMapper implements Function<Death, DeathdtoResponse> {

    @Override
    public DeathdtoResponse apply(Death death) {
        return new DeathdtoResponse(death.getId(),death.getNumeroCertificat(),death.getDateRegistre(),death.getDateDeces(),
                death.getLieuDeces(),death.getNomComplet(),death.getLieuNaissance(),death.getDateNaissance(),death.getProfession(),death.getNomPere(),
                death.getNomMere(),death.getDateDelivrance());
    }
}
