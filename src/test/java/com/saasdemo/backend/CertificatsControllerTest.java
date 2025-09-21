package com.saasdemo.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasdemo.backend.controller.CertificatsController;
import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.service.CertificatServices;
import com.saasdemo.backend.service.PdfService;




@WebMvcTest(controllers = CertificatsController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                                        SecurityFilterAutoConfiguration.class})
class CertificatsControllerTest {

     @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;


    

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CertificatServices certificatServices; // service utilisé par le controller

    @MockBean
    private PdfService pdfService; // service utilisé par le controller

    @Test
    void testBirthCertificateCreation() throws Exception {
        BirthDtoRequest birthDto = new BirthDtoRequest();
        birthDto.setNomComplet("Achoyatte");
        birthDto.setEmail("achoyatte@gmail.com");

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusMsg("Certificat créé avec succès");

        Mockito.when(certificatServices.BirthCreate(Mockito.any(BirthDtoRequest.class)))
               .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/birthCertificatecreation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(birthDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Certificat créé avec succès"));
    }
}
