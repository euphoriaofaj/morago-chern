package com.morago.backend.repository;

import com.morago.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.translator.id = :translatorId")
    Double findAverageRatingByTranslatorId(@Param("translatorId") Long translatorId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.translator.id = :translatorId")
    Long countByTranslatorId(@Param("translatorId") Long translatorId);
}
