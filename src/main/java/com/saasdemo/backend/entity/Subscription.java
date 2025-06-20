package com.saasdemo.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    
    private String status; // active, expired, pending
    private LocalDateTime createdAt=LocalDateTime.now() ;
    private LocalDateTime endDate;
    private String Reference;
    private String planCode; 
    private String name;
    private String interval;
    private int amount; // en kobo (ex: 500000 = 5000 FCFA)

   @OneToOne
    @JoinColumn(name = "commune_id")
    private area commune;

   


}