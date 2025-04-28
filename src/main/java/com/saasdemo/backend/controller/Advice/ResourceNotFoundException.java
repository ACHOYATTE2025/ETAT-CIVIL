package com.saasdemo.backend.controller.Advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


// gestion des messages d'exeption
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    
    public ResourceNotFoundException(String message){
       super (message);
    }
}