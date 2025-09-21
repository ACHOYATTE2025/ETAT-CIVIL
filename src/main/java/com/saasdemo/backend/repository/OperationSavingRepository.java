package com.saasdemo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.OperationsSaving;

@Repository
public interface OperationSavingRepository extends JpaRepository<OperationsSaving,Long>{
    
}