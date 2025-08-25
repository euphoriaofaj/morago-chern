package com.morago.backend.repository;

import com.morago.backend.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    
    @Query("SELECT COUNT(c) FROM Call c WHERE c.recipient.id = :translatorId AND c.status = true")
    Long countCompletedCallsByTranslatorId(@Param("translatorId") Long translatorId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Call c " +
           "WHERE c.recipient.id = :translatorId AND c.callStatus IN ('CONNECT_NOT_SET', 'SUCCESSFUL') AND c.isEndCall = false")
    Boolean existsActiveCallByTranslatorId(@Param("translatorId") Long translatorId);
}
