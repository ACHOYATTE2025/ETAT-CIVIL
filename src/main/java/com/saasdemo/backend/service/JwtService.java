package com.saasdemo.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saasdemo.backend.dto.ActiveCodeRequest;
import com.saasdemo.backend.dto.ResponseDto;
import com.saasdemo.backend.dto.SignupResponse;
import com.saasdemo.backend.entity.Jwt;
import com.saasdemo.backend.entity.RefreshToken;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.JwtRepository;
import com.saasdemo.backend.repository.RefreshTokenRepository;
import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JwtService {

    private final UtilisateurRepository utilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtRepository jwtRepository;
    private final JwtUtil jwtUtil;
    



    public SignupResponse generateAndSaveToken(Utilisateur utilisateur) {
        // désactiver anciens tokens
        disableToken(utilisateur);

        // générer nouveau JWT
        String jwtBearer = jwtUtil.generateToken(utilisateur);
        

        // refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis( 36000000))
                .build();

        // save en DB
        Jwt jwtbuild = Jwt.builder()
                .valeur(jwtBearer)
                .desactive(false)
                .expiration(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();

        jwtRepository.save(jwtbuild);

        return new SignupResponse(jwtBearer, refreshToken.getValeur());
    }

    public void disableToken(Utilisateur utilisateur) {
        List<Jwt> jwtList = jwtRepository.findByUtilisateur(utilisateur.getEmail())
                .peek(jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpiration(true);
                }).toList();
        jwtRepository.saveAll(jwtList);
    }

    @Scheduled(cron = "@daily")
    public void removeUselessToken() {
        log.info("Suppression tokens invalid {}", Instant.now());
        jwtRepository.deleteByExpirationAndDesactive(true, true);
    }



    

    //production de refresh Token
     public SignupResponse refreshtoken(ActiveCodeRequest refreshTokenRequest) {
        final Jwt jwt= this.jwtRepository.findByRefreshToken(refreshTokenRequest.getCode())
        .orElseThrow(()-> new RuntimeException("REFRESH-TOKEN Invalid"));

        if(jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now()))
        { throw new RuntimeException("REFRESH-TOKEN EXPIRED "); };

        RefreshToken refresh= this.refreshTokenRepository.findByValeur(refreshTokenRequest.getCode())
        .orElseThrow(()-> new RuntimeException("REFRESH-TOKEN INCONU"));
        
        SignupResponse tokens=  this.generateAndSaveToken(jwt.getUtilisateur());
        refresh.setExpire(true);
        this.refreshTokenRepository.save(refresh);
        
        return tokens; }
     
//deconnexion d'un User ou Admin 
     public ResponseEntity<ResponseDto> deconex() {
    try {
        Utilisateur user =  (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Jwt> jwtOpt = jwtRepository.findByUtilisateurAndExpirationFalseAndDesactiveFalse(user);

        if (jwtOpt.isPresent()) {
            Jwt jwt = jwtOpt.get();

            if (!jwt.getDesactive() && !jwt.getExpiration()) {
                disableToken(user);
                jwt.getRefreshToken().setExpire(true);
                refreshTokenRepository.save(jwt.getRefreshToken());
                user.setConnected(false);
                this.utilisateurRepository.save(user);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseDto(200, user.getUsername() + " EST DECONNECTE"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDto(401, user.getUsername() + " EST DEJA DECONNECTE"));
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto(404, "JWT introuvable pour l'utilisateur"));
        }
    } catch (Exception e) {
        log.error("Erreur lors de la déconnexion : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto(500, e.getMessage()));
    }
}

    
}
