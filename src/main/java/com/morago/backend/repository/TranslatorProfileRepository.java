package com.morago.backend.repository;

import com.morago.backend.entity.TranslatorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TranslatorProfileRepository extends JpaRepository<TranslatorProfile, Long> {
    
    boolean existsByUserId(Long userId);
    
    @Query("SELECT tp FROM TranslatorProfile tp " +
           "LEFT JOIN tp.languages l " +
           "LEFT JOIN tp.themes t " +
           "WHERE (:isAvailable IS NULL OR tp.isAvailable = :isAvailable) " +
           "AND (:isOnline IS NULL OR tp.isOnline = :isOnline) " +
           "AND (:languageId IS NULL OR l.id = :languageId) " +
           "AND (:themeId IS NULL OR t.id = :themeId) " +
           "AND (:levelOfKorean IS NULL OR tp.levelOfKorean = :levelOfKorean)")
    Page<TranslatorProfile> findWithFilters(
            @Param("isAvailable") Boolean isAvailable,
            @Param("isOnline") Boolean isOnline,
            @Param("languageId") Long languageId,
            @Param("themeId") Long themeId,
            @Param("levelOfKorean") String levelOfKorean,
            Pageable pageable
    );
    
    @Query("SELECT tp FROM TranslatorProfile tp " +
           "JOIN tp.themes t " +
           "WHERE t.id = :themeId " +
           "AND tp.isAvailable = true " +
           "AND tp.isOnline = true")
    Page<TranslatorProfile> findAvailableByTheme(@Param("themeId") Long themeId, Pageable pageable);
}
