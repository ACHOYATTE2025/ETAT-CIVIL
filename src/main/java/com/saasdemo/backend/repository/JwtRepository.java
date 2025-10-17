package com.saasdemo.backend.repository;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Jwt;
import com.saasdemo.backend.entity.Utilisateur;




@Repository
public interface JwtRepository extends CrudRepository<Jwt, Long> {
    Optional<Jwt> findByValeur(String valeur);


    void deleteByExpirationAndDesactive(Boolean expiration,Boolean desactive);


    void deleteByValeur(String valeur);

    @Query("FROM Jwt j WHERE  j.utilisateur.email=:email and j.desactive= :expire and j.expiration=: expire" )
    Optional <Jwt> findBytoken(String email, Boolean desactive,Boolean expire);

    @Query("FROM Jwt j WHERE  j.utilisateur.email=:email")
    Stream <Jwt> findByUtilisateur(String email);

    @Query("FROM Jwt j WHERE  j.refreshToken.valeur=:valeur")
    Optional <Jwt> findByRefreshToken(String valeur);


    void deleteAllByValeur(String valeur);

    Optional<Jwt>  findByUtilisateur(Utilisateur utilisateur);

      Optional<Jwt> findByUtilisateurAndExpirationFalseAndDesactiveFalse(Utilisateur user);
    


    
   
}
