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

    // Generate and save JWT and Refresh Token for a user
    public SignupResponse generateAndSaveToken(Utilisateur utilisateur) {
        log.info("Generating new JWT and RefreshToken for user: {}", utilisateur.getEmail());

        // Disable old tokens
        disableToken(utilisateur);

        // Generate new JWT
        String jwtBearer = jwtUtil.generateToken(utilisateur);
        log.debug("Generated JWT for {}: {}", utilisateur.getEmail(), jwtBearer);

        // Create refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(36000000))
                .build();

        // Save to DB
        Jwt jwtbuild = Jwt.builder()
                .valeur(jwtBearer)
                .desactive(false)
                .expiration(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();

        jwtRepository.save(jwtbuild);
        log.info("JWT and RefreshToken saved for user {}", utilisateur.getEmail());

        return new SignupResponse(jwtBearer, refreshToken.getValeur());
    }

    // Disable all tokens for a user
    public void disableToken(Utilisateur utilisateur) {
        log.info("Disabling all tokens for user: {}", utilisateur.getEmail());

        List<Jwt> jwtList = jwtRepository.findByUtilisateur(utilisateur.getEmail())
                .peek(jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpiration(true);
                    log.debug("Token disabled: {}", jwt.getValeur());
                }).toList();

        jwtRepository.saveAll(jwtList);
        log.info("{} token(s) disabled for user {}", jwtList.size(), utilisateur.getEmail());
    }

    // Scheduled task to remove invalid tokens daily
    @Scheduled(cron = "@daily")
    public void removeUselessToken() {
        log.info("Deleting invalid tokens at {}", Instant.now());
        int deletedCount = jwtRepository.deleteByExpirationAndDesactive(true, true);
        log.info("{} invalid token(s) deleted", deletedCount);
    }

    // Generate new refresh token
    public SignupResponse refreshtoken(ActiveCodeRequest refreshTokenRequest) {
        log.info("Refreshing JWT with RefreshToken: {}", refreshTokenRequest.getCode());

        final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.getCode())
                .orElseThrow(() -> {
                    log.warn("RefreshToken not found: {}", refreshTokenRequest.getCode());
                    return new RuntimeException("REFRESH-TOKEN Invalid");
                });

        if (jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            log.warn("Expired RefreshToken for user {}", jwt.getUtilisateur().getEmail());
            throw new RuntimeException("REFRESH-TOKEN EXPIRED ");
        }

        RefreshToken refresh = this.refreshTokenRepository.findByValeur(refreshTokenRequest.getCode())
                .orElseThrow(() -> {
                    log.warn("Unknown RefreshToken: {}", refreshTokenRequest.getCode());
                    return new RuntimeException("REFRESH-TOKEN UNKNOWN");
                });

        SignupResponse tokens = this.generateAndSaveToken(jwt.getUtilisateur());
        refresh.setExpire(true);
        this.refreshTokenRepository.save(refresh);

        log.info("RefreshToken rotated for user {}", jwt.getUtilisateur().getEmail());
        return tokens;
    }

    // Logout a User or Admin
    public ResponseEntity<ResponseDto> deconex() {
        try {
            Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Attempting logout for user {}", user.getEmail());

            Optional<Jwt> jwtOpt = jwtRepository.findByUtilisateurAndExpirationFalseAndDesactiveFalse(user);

            if (jwtOpt.isPresent()) {
                Jwt jwt = jwtOpt.get();

                if (!jwt.getDesactive() && !jwt.getExpiration()) {
                    disableToken(user);
                    jwt.getRefreshToken().setExpire(true);
                    refreshTokenRepository.save(jwt.getRefreshToken());
                    user.setConnected(false);
                    this.utilisateurRepository.save(user);

                    log.info("User {} successfully logged out", user.getEmail());
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseDto(200, user.getUsername() + " HAS BEEN LOGGED OUT"));
                } else {
                    log.warn("Logout failed: user {} already logged out", user.getEmail());
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseDto(401, user.getUsername() + " IS ALREADY LOGGED OUT"));
                }
            } else {
                log.warn("No active JWT found for user {}", user.getEmail());
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(404, "JWT not found for the user"));
            }
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(500, e.getMessage()));
        }
    }
}
