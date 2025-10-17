package com.saasdemo.backend.service;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.entity.Validation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
    private JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendNotification(Validation validation) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreplyetatcivilx@gmail.com");
            helper.setTo(validation.getUtilisateur().getEmail());
            helper.setSubject("🔑 Code de validation By ETAT _CIVIL APP");

            // HTML pro amélioré
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<body style=\"font-family:Arial,sans-serif; background-color:#f4f6f8; margin:0; padding:0;\">"
                    + "<div style=\"max-width:600px; margin:50px auto; background-color:white; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.1); padding:30px;\">"
                    + "<div style=\"text-align:center;\">"
                    + "<img src='https://i.ibb.co/7r2J8xR/logo.png' alt='Etat Civil App Logo' width='100' style='margin-bottom:20px;'/>"
                    + "<h2 style=\"color:#2E86C1; margin-bottom:10px;\">Etat Civil App</h2>"
                    + "<p style=\"color:#555; font-size:16px;\">Bonjour <strong>" + validation.getUtilisateur().getUsername() + "</strong>,</p>"
                    + "<p style=\"margin:20px 0; font-size:16px;\">Voici votre code d'activation :</p>"
                    + "<div style=\"font-size:28px; font-weight:bold; color:#E74C3C; background:#f1f1f1; padding:15px 0; border-radius:6px; letter-spacing:4px;\">"
                    + validation.getCode()
                    + "</div>"
                    + "<p style=\"margin-top:20px; font-size:14px; color:#888;\">Valable 10 minutes</p>"
                    + "<a href=\"https://etatcivil.com/login\" "
                    + "style=\"display:inline-block; margin-top:25px; padding:12px 25px; background-color:#2E86C1; color:white; text-decoration:none; border-radius:5px; font-weight:bold;\">Se connecter</a>"
                    + "<p style=\"margin-top:30px; font-size:12px; color:#aaa;\">Si vous n'avez pas demandé ce code, ignorez ce message.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email HTML professionnel envoyé à {} avec code {}", validation.getUtilisateur().getEmail(), validation.getCode());

        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email HTML", e);
        }

    }
}
