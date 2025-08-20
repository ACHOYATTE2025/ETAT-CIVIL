package com.saasdemo.backend.entity;

import java.time.Instant;

import com.saasdemo.backend.enums.TypeOperation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "operationsaving")
public class OperationsSaving {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  private String name;

  @NotNull
  private String email;

 
  @Enumerated(value = EnumType.STRING)
  private TypeOperation operationNature;

  @NotNull
  private Instant operationDate;
}