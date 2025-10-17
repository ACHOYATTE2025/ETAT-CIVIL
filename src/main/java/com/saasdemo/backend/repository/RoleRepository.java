package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.enums.TypeRole;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
 

 Optional< Role> findByLibele(TypeRole user);

}