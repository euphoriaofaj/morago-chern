package com.morago.backend.dto.response;

import com.morago.backend.dto.LanguageDto;
import com.morago.backend.dto.ThemeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for translator profile information")
public class TranslatorProfileResponse {

    @Schema(description = "Profile ID", example = "1")
    private Long id;

    @Schema(description = "User ID this profile belongs to", example = "1")
    private Long userId;

    @Schema(description = "Translator's first name", example = "John")
    private String firstName;

    @Schema(description = "Translator's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Translator's username/phone", example = "01012345678")
    private String username;

    @Schema(description = "Translator's date of birth", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Translator's email address", example = "translator@example.com")
    private String email;

    @Schema(description = "Level of Korean proficiency", example = "Advanced")
    private String levelOfKorean;

    @Schema(description = "Whether the translator is available for calls", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Whether the translator is currently online", example = "false")
    private Boolean isOnline;

    @Schema(description = "Profile creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Profile last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Languages the translator speaks")
    private Set<LanguageDto> languages;

    @Schema(description = "Themes the translator specializes in")
    private Set<ThemeDto> themes;

    @Schema(description = "Average rating from users", example = "4.5")
    private Double averageRating;

    @Schema(description = "Total number of completed calls", example = "150")
    private Long totalCalls;

    @Schema(description = "Total number of ratings received", example = "120")
    private Long totalRatings;
}