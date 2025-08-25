package com.morago.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Summary response DTO for translator profile (used in lists)")
public class TranslatorProfileSummaryResponse {

    @Schema(description = "Profile ID", example = "1")
    private Long id;

    @Schema(description = "User ID this profile belongs to", example = "1")
    private Long userId;

    @Schema(description = "Translator's full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Translator's email address", example = "translator@example.com")
    private String email;

    @Schema(description = "Level of Korean proficiency", example = "Advanced")
    private String levelOfKorean;

    @Schema(description = "Whether the translator is available for calls", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Whether the translator is currently online", example = "false")
    private Boolean isOnline;

    @Schema(description = "Language names the translator speaks", example = "[\"English\", \"Korean\", \"Japanese\"]")
    private Set<String> languageNames;

    @Schema(description = "Theme names the translator specializes in", example = "[\"Medical\", \"Legal\", \"Business\"]")
    private Set<String> themeNames;

    @Schema(description = "Average rating from users", example = "4.5")
    private Double averageRating;

    @Schema(description = "Total number of completed calls", example = "150")
    private Long totalCalls;

    @Schema(description = "Whether this translator is currently in a call", example = "false")
    private Boolean inCall;
}