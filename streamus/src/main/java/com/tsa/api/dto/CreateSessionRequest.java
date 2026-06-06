package com.streamus.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(

        @NotBlank(message = "Youtube URL is required")
        String youtubeUrl,

        @NotBlank(message = "Target language is required")
        String targetLanguage

) {
}