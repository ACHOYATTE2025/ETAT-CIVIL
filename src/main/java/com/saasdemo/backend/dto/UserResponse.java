package com.saasdemo.backend.dto;

import com.saasdemo.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    private int statusCode;
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String communeName;
 
    
}