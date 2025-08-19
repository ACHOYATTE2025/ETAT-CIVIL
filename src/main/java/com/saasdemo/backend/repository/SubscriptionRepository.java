package com.saasdemo.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saasdemo.backend.entity.Area;
import com.saasdemo.backend.entity.Subscription;



@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    //check if subscription is vzlid
    @Query("SELECT s FROM Subscription s WHERE s.commune.id = :orgId AND s.status = 'active' AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByCommuneId(@Param("orgId") Long orgId);

    boolean existsByCommuneAndActiveTrue(Area commune);

    Optional <Subscription>  findById(Long id);

   Optional <Subscription> findByUsersName(String usersName);

}
