package com.saasdemo.backend.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.entity.Validation;
import com.saasdemo.backend.enums.GenderSLC;
import com.saasdemo.backend.repository.ValidationRepository;

@Service
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;
    public String codeX = "";
    public Utilisateur accountX = new Utilisateur();
    private static final Logger logger = Logger.getLogger(ValidationService.class.getName());

    /**
     * Constructor for ValidationService.
     * @param validationRepository Repository for validation entities.
     * @param notificationService Service to send notifications.
     */
    public ValidationService(ValidationRepository validationRepository, NotificationService notificationService) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Generates a code for the user and saves the validation entity.
     * Sends a notification to the user.
     * @param subscriber The user for whom the code is created.
     * @param signup The action type (SIGNUP, LOGIN, USER_CREATION).
     */
    public void createCode(Utilisateur subscriber, GenderSLC signup) {
        logger.info(() -> "Starting createCode for user: " + subscriber.getEmail());

        Validation validation = new Validation();
        validation.setUtilisateur(subscriber);
        this.accountX = subscriber;

        // Set the action type
        switch (signup) {
            case SIGNUP -> {
                validation.setGenreSlc(GenderSLC.SIGNUP);
                logger.fine("Action type set to SIGNUP");
            }
            case LOGIN -> {
                validation.setGenreSlc(GenderSLC.LOGIN);
                logger.fine("Action type set to LOGIN");
            }
            default -> {
                validation.setGenreSlc(GenderSLC.USER_CREATION);
                logger.fine("Action type set to USER_CREATION");
            }
        }

        // Set creation and expiration times
        Instant now = Instant.now();
        validation.setCreationCode(now);
        validation.setExpirationCode(now.plus(10, ChronoUnit.MINUTES));
        logger.fine("Validation timestamps set: created=" + now + ", expires=" + validation.getExpirationCode());

        // Generate random code (6 digits, padded with zeros if needed)
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1_000_000));
        validation.setCode(code);
        this.codeX = code;
        logger.info("Generated validation code: " + code);

        // Save validation
        try {
            this.validationRepository.save(validation);
            logger.info("Validation entity saved successfully for user: " + subscriber.getEmail());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving validation for user: " + subscriber.getEmail(), e);
            throw e;
        }

        // Send notification
        try {
            this.notificationService.sendNotification(validation);
            logger.info("Notification sent successfully for code: " + code);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send notification for code: " + code, e);
            throw e;
        }
    }

    /**
     * Retrieves the validation entity based on code.
     * @param code The code to validate.
     * @return Validation entity if found.
     * @throws RuntimeException if the code is invalid.
     */
    public Validation getValidation(String code) {
        logger.info("Attempting to retrieve validation for code: " + code);

        return this.validationRepository.findByCode(code)
            .map(validation -> {
                logger.info("Validation found for code: " + code + ", user: " + validation.getUtilisateur().getEmail());
                return validation;
            })
            .orElseThrow(() -> {
                logger.warning("Validation not found or invalid for code: " + code);
                return new RuntimeException("Invalid code");
            });
    }
}
