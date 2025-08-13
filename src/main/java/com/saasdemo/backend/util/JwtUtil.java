package com.saasdemo.backend.util;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.saasdemo.backend.entity.Jwt;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.JwtRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;




@Component
@Slf4j
@Transactional
public class JwtUtil {

private final JwtRepository jwtRepository;


public JwtUtil(JwtRepository jwtRepository){
  this.jwtRepository= jwtRepository;
 
}
  
  @Value("${jwt.key}")
  private   String jwtSecret;


  //20min
  @Value("${jwt.expiration}")
  private Long jwtexpiration;

  public String generateToken( Utilisateur utilisateur){
    this.disableToken(utilisateur);//desactiver le token actif 
    String jwtbearer = Jwts.builder()
          .setSubject(utilisateur.getEmail())
          .claim("role",utilisateur.getRole())
          .claim("communeId",utilisateur.getCommune().getId())
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis()+jwtexpiration))
          .signWith(SignatureAlgorithm.HS256,jwtSecret)
          .compact();


      Jwt jwtbuild =
          Jwt.builder()
                  .valeur(jwtbearer.substring(0,jwtbearer.length()-1))
                  .desactive(false)
                  .expiration(false)
                  .utilisateur(utilisateur)
                  .build();
  this.jwtRepository.save(jwtbuild);

  return jwtbearer;
  }


  //methods claims
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
    .setSigningKey(jwtSecret)
    .build()
    .parseClaimsJws(token)
    .getBody();
}

private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
  Claims claims = extractAllClaims(token);
  return claimsResolver.apply(claims);
}

public Boolean isTokenExpired(String token) {
  Date expirationDate = this.getClaim(token, Claims::getExpiration);
  return expirationDate.before(new Date());

}

public String extractUsername(String token) {return this.getClaim(token, Claims::getSubject);
}

public Jwt tokenByValue(String token) {
  return (Jwt) jwtRepository.findByValeur(token).orElseThrow(() -> new RuntimeException("Token invalid"));
}

//end se deconnecter
public ResponseEntity<?> deconex() {
  Utilisateur utix = new Utilisateur();
  ResponseEntity avad = null;
  Optional<Jwt> jx = Optional.ofNullable(new Jwt());
  try{    

      Utilisateur sub=  (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      utix=sub;
      jx =jwtRepository.findByUtilisateur(sub);}
  catch(Exception e){ e.getLocalizedMessage();}
  System.out.println("  DESACT "+jx.get().getDesactive() +" " + " EXPIRA "+jx.get().getExpiration());
 if(jx.get().getDesactive()!=true && jx.get().getExpiration()!=true){
    disableToken(utix);
    avad= ResponseEntity.ok().body(utix.getUsername() +" EST DECONNECTE");}
  else {avad=ResponseEntity.badRequest().body(utix.getUsername()+" EST DEJA DECONNECTE");}
    return  avad;
 }
 
   





//suppresion journalière des tokens dans la base données
 @Scheduled(cron = "@hourly")
    public void removeUselessToken(){
        log.info("supression Token invalid{}", Instant.now());
        this.jwtRepository.deleteByExpirationAndDesactive(true,true);
    }


    // fonction pour desactiver le token actif 
        public void  disableToken(Utilisateur subscriber){
        final List<Jwt> jwtList = this.jwtRepository.findByUtilisateur(subscriber.getEmail()).
                peek(
                        jwt -> {
                            jwt.setDesactive(true);
                            jwt.setExpiration(true);
                        }
                ).toList();
        this.jwtRepository.saveAll(jwtList);


    
}
}


