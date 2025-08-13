package com.saasdemo.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


//configuration de swagger avec JWT 
@Configuration
@OpenAPIDefinition(
   security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT" 
)
public class SwaggerConfig {

  @org.springframework.context.annotation.Bean
  public OpenAPI CustomOpenApi(){
    return new OpenAPI()
        .info(new Info()
        .title("API Gestion des États Civils")
        .version("1.0")
        .description("Documentation interactive de l'API de gestion des naissances, mariages, décès, etc."));
        
  }

   
    
}