package com.saasdemo.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.enums.StatutAbonnement;



@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  // Vérifie si la souscription est valide (ACTIVE ou TRIAL) et non expirée
    @Query("SELECT s FROM Subscription s " +
       "WHERE s.commune = :commune " +
       "AND s.status IN ('ACTIVE', 'TRIAL') " +
       "AND s.endDate > CURRENT_TIMESTAMP")
Optional<Subscription> findActiveByCommune(@Param("commune") Area commune);


  boolean existsByCommuneAndActiveTrueAndEndDateAfter(Area commune,LocalDateTime locadate);

 
  Optional <Subscription> findByUsersName(String usersName);

  List<Subscription> findByUsersNameAndActiveTrueAndEndDateBefore( String name,LocalDateTime now);

  Optional<Subscription> findByUsersNameAndEndDateBefore( String name,LocalDateTime now);

  Optional<Subscription> findByUsersNameAndEndDateAfter( String name,LocalDateTime now);

  Optional <Subscription>  findByUsersNameAndActiveTrueAndEndDateAfterAndStatus(String ado,LocalDateTime lol,StatutAbonnement stat);

  List<Subscription> findAllByCommune(Area commune);





  Optional<Subscription> findByUsersNameAndActiveTrueAndEndDateAfter(String username, LocalDateTime now);
    
    Page<Subscription> findAllByCommune(Area commune, Pageable pageable);

    // Filtrer par date de début (created)
    Page<Subscription> findAllByCommuneAndCreatedAfter(Area commune, LocalDateTime startDate, Pageable pageable);


}
