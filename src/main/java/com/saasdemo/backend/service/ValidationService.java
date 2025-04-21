package com.saasdemo.backend.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Validation;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.repository.ValidationRepository;

@Service
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;
    public String codeX ="";
    public Utilisateur accountX = new Utilisateur();

    public ValidationService(ValidationRepository validationRepository, NotificationService notificationService) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;


    }

    //code making
    public void createCode(Utilisateur subscriber, GenderSLC signup ) {

        Validation validation = new Validation();
        validation.setUtilisateur(subscriber);
        this.accountX=subscriber;
        if(signup.equals(GenderSLC.SIGNUP)){
            validation.setGenreSlc(GenderSLC.SIGNUP);
        }
        else if(signup.equals(GenderSLC.LOGIN)){validation.setGenreSlc(GenderSLC.LOGIN);}
        else{validation.setGenreSlc(GenderSLC.USER_CREATION);}
       


        Instant now = Instant.now();
        validation.setCreationCode(now);
        validation.setExpirationCode(now.plus(10, ChronoUnit.MINUTES));


        Random random = new Random();
        random.nextInt(999999);
        String code = String.valueOf(random.nextInt(999999));
        validation.setCode(code);
        this.codeX = code;

        this.validationRepository.save(validation);
        this.notificationService.sendNotification(validation);

    }

    //verifiez la validation du code
    public Validation getValidation(String code) {
        return this.validationRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Invalid code"));

    };

}
