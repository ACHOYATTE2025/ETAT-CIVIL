package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.saasdemo.backend.entity.Subscription;


public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    //check if subscription is vzlid
    @Query("SELECT s FROM Subscription s WHERE s.commune.id = :orgId AND s.status = 'active' AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByCommuneId(@Param("orgId") Long orgId);
  
}
