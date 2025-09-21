package com.saasdemo.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.saasdemo.backend.dto.BirthDtoRequest;
import com.saasdemo.backend.dto.BirthDtoResponse;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Birth;
import com.saasdemo.backend.entity.OperationsSaving;
import com.saasdemo.backend.entity.Registre;
import com.saasdemo.backend.entity.Role;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.enums.TypeRole;
import com.saasdemo.backend.mapper.BirthDtoMapper;
import com.saasdemo.backend.repository.BirthRepository;
import com.saasdemo.backend.repository.OperationSavingRepository;
import com.saasdemo.backend.repository.RegistreRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.service.CertificatServices;

@ExtendWith(MockitoExtension.class)
public class CertificateServiceTest {
    
    

// read a birth certificate by Id

    @InjectMocks
    private CertificatServices certificatServices;

    @Mock
    private BirthRepository birthRepository;

    @Mock
    private OperationSavingRepository operationsSavingRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RegistreRepository registreRepository;

    @Mock
    private BirthDtoMapper birthDtoMapper;


    
    OperationsSaving operationsSaving;

    Birth birth;
    Area area;
    Role role;
   
    


    
    @BeforeEach
void setUpEntities() {
    // Role
    role = Role.builder()
               .id(1L)
               .libele(TypeRole.USER)
               .build();

    // Area
    area = Area.builder()
               .id(1L)
               .nameCommune("Alepe")
               .build();

    // OperationsSaving
    operationsSaving = OperationsSaving.builder()
        .id(1L)
        .name("Test Operation")
        .NumeroActe(UUID.randomUUID().toString())
        .email("acho@gmail.com")
        .build();

   

}

 //registre
    Registre registre =Registre.builder()
                .id(1L)
                .registreAnnee("20225")
                .build();

 
    // Certificat fictif
  Birth fakeCert = Birth.builder()
            .id(1L)
            .email("acho@gmail.com")
            .nomComplet("ACHO")
            .commune(area)
            .registre(registre)
            .build();


    // Créer un utilisateur factice (comme celui qui serait dans le JWT)
   void setupSecurityContext() {
    Utilisateur user = Utilisateur.builder()
            .email("acho@gmail.com")
            .password("password")
            .role(role)
            .commune(area)
            .build();

    // Mock du repository pour éviter le NPE ou ce Mock n'est pas obligatoire
    lenient().when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
}



  
    BirthDtoRequest fakeDto = BirthDtoRequest.builder()
        .email("acho@gmail.com")
        .nomComplet("ACHO")
        .build();
  



        

    @Test
    void testGetCertificateById_Found() {
        when(birthDtoMapper.apply(any(Birth.class))).thenReturn(
    new BirthDtoResponse(
        fakeCert.getNumeroExtrait(),
        fakeCert.getLieuDelivrance(),
        fakeCert.getDateDelivrance(),
        fakeCert.getNomComplet(),
        fakeCert.getLieuNaissance(),
        fakeCert.getDateNaissance(),
        fakeCert.getNomPere(),
        fakeCert.getProfessionPere(),
        fakeCert.getDomicilePere(),
        fakeCert.getNationalitePere(),
        fakeCert.getNomMere(),
        fakeCert.getProfessionMere(),
        fakeCert.getDomicileMere(),
        fakeCert.getNationaliteMere(),
        fakeCert.getMarie(),
        fakeCert.getMarieAvec(),
        fakeCert.getNumeroDecisionDM(),
        fakeCert.getDissolutionMariage(),
        fakeCert.getDeces()
    )
);


        when(birthRepository.findById(1L)).thenReturn(Optional.of(fakeCert));

        Optional<BirthDtoResponse> result = certificatServices.ReadBirthById(1L);

        assertTrue(result.isPresent());
        assertEquals("ACHO", result.get().getNomComplet());
    }
    


    

    @Test
    void testGetCertificateById_NotFound() {
        // Arrange : aucun certificat trouvé
        when(birthRepository.findById(99L)).thenReturn(Optional.empty());

     // Act & Assert : vérifier que l’exception est bien lancée
    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> certificatServices.ReadBirthById(99L)
    );

    assertEquals("EXTRAIT INEXISTANT!!!", exception.getMessage());
    }



  // test Birth certificate Creation

   
  @Test
  void testBirthCreate(){
         // Simuler le contexte de sécurité
    setupSecurityContext();

    // Arrange : mock du save
    when(birthRepository.save(any(Birth.class))).thenReturn(fakeCert);

    // Act
    ResponseEntity<ResponseDto> response = certificatServices.BirthCreate(fakeDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
   
}
    
}