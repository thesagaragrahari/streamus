package com.tsa.session;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.streamus.api.dto.CreateSessionRequest;
import com.streamus.api.dto.CreateSessionResponse;
import com.streamus.session.entity.StreamSession;
import com.streamus.session.model.SessionStatus;
import com.streamus.session.repository.SessionRepository;
import com.streamus.session.service.SessionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public Mono<CreateSessionResponse> createSession(
            CreateSessionRequest request) {

        String sessionId = UUID.randomUUID().toString();

        StreamSession session = StreamSession.builder()
                .sessionId(sessionId)
                .youtubeUrl(request.youtubeUrl())
                .targetLanguage(request.targetLanguage())
                .status(SessionStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        return sessionRepository.save(session)
                .map(savedSession ->
                        new CreateSessionResponse(
                                savedSession.getSessionId(),
                                savedSession.getStatus().name()));
    }
}