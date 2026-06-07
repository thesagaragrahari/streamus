package com.tsa.session.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.tsa.session.model.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stream_session")
public class StreamSession {

    @Id
    private Long id;

    private String sessionId;

    private String youtubeUrl;

    private String targetLanguage;

    private SessionStatus status;

    private LocalDateTime createdAt;
}