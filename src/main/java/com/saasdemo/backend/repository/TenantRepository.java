package com.saasdemo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saasdemo.backend.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant,String>{
    
}