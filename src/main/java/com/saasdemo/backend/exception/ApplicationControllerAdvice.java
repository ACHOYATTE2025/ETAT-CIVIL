package com.saasdemo.backend.exception;


import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.saasdemo.backend.dto.ErrorResponseDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {


    //manage all exception !!!!!
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(Exception exception,WebRequest webRequest){
        ErrorResponseDto errorDto= new ErrorResponseDto(
        webRequest.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR ,
        exception.getMessage(),
        LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorDto);
            
            
    }
}
