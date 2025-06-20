package com.saasdemo.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tenants")
public class Tenant {
  @Id
    private String id;

    private String name;
    @NaturalId
    private String email;

    private String abonnementStatut;

    private LocalDateTime abonnementExpireLe;
    
    private Boolean active=false;

    
}