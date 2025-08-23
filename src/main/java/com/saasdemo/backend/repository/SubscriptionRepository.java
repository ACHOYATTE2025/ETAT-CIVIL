package com.saasdemo.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.enums.StatutAbonnement;



@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    //check if subscription is vzlid
    @Query("SELECT s FROM Subscription s WHERE s.commune.id = :orgId AND s.status = 'active' AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByCommuneId(@Param("orgId") Long orgId);

    boolean existsByCommuneAndActiveTrueAndEndDateAfter(Area commune,LocalDateTime locadate);

    Optional <Subscription>  findById(Long id);

   Optional <Subscription> findByUsersName(String usersName);

   List<Subscription> findByUsersNameAndActiveTrueAndEndDateBefore( String name,LocalDateTime now);

  Optional <Subscription>  findByUsersNameAndActiveTrueAndEndDateAfterAndStatus(String ado,LocalDateTime lol,StatutAbonnement stat);






}
