package com.saasdemo.backend;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.saasdemo.backend.controller.AuthController;
import com.saasdemo.backend.dto.SignupRequest;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.SubscriptionService;
import com.saasdemo.backend.service.UserService;
import com.saasdemo.backend.util.JwtUtil;

@SpringBootTest
class SaasBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}

@WebMvcTest(AuthController.class)
class AuthControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Mock
    AuthService authService;

    @Mock
    UserService userService;

    @Mock
    JwtUtil jwtUtil;
    
    @Mock
    SubscriptionService subscriptionService;

   
    

    @Test
    void registerAdmin(){
         SignupRequest request = new SignupRequest();
        request.setUsername("admin");
        request.setEmail("admin@example.com");
        request.setPassword("123456");

        // Red : on configure le mock pour retourner une réponse simulée
       /* when(authService.Register(request))
                .thenReturn((ResponseEntity<?>) ResponseEntity.ok("User registered successfully"));

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"email\":\"admin@example.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));*/
    }



    }

