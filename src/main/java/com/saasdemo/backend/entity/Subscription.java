package com.saasdemo.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.saasdemo.backend.enums.StatutAbonnement;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Pour stocker l'énum en texte
    private StatutAbonnement status; // active, expired, pending,trial
    private LocalDateTime createdAt ;
    private LocalDateTime endDate;
    private BigDecimal amount; //en franc cfa

    @Builder.Default
    private Boolean active=false;

    @OneToOne
    private area commune;

    @OneToOne
    @JoinColumn(name = "utilisateurId")
    private Utilisateur utilisateur;

    

   


}