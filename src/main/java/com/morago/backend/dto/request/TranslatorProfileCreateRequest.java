package com.morago.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a translator profile")
public class TranslatorProfileCreateRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user this profile belongs to", example = "1")
    private Long userId;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Translator's date of birth", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    @Schema(description = "Translator's email address", example = "translator@example.com")
    private String email;

    @Size(max = 200, message = "Korean level description must not exceed 200 characters")
    @Schema(description = "Level of Korean proficiency", example = "Advanced", allowableValues = {"Beginner", "Intermediate", "Advanced", "Native"})
    private String levelOfKorean;

    @Builder.Default
    @Schema(description = "Whether the translator is available for calls", example = "true")
    private Boolean isAvailable = false;

    @Builder.Default
    @Schema(description = "Whether the translator is currently online", example = "false")
    private Boolean isOnline = false;

    @Schema(description = "Set of language IDs the translator speaks", example = "[1, 2, 3]")
    private Set<Long> languageIds;

    @Schema(description = "Set of theme IDs the translator specializes in", example = "[1, 2, 3]")
    private Set<Long> themeIds;
}