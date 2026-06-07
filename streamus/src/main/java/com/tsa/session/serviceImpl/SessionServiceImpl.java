package com.tsa.session.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tsa.api.dto.CreateSessionRequest;
import com.tsa.api.dto.CreateSessionResponse;
import com.tsa.session.entity.StreamSession;
import com.tsa.session.model.SessionStatus;
import com.tsa.session.repository.SessionRepository;
import com.tsa.session.service.SessionService;

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
                                .map(savedSession -> new CreateSessionResponse(
                                                savedSession.getSessionId(),
                                                savedSession.getStatus().name()));
        }

        @Override
        public Mono<CreateSessionResponse> getSession(String sessionId) {
                return sessionRepository.findBySessionId(sessionId)
                                .map(session -> new CreateSessionResponse(
                                                session.getSessionId(),
                                                session.getStatus().name()));

        }


        @Override
        public Mono<StreamSession> updateStatus(String sessionId, SessionStatus status) {
                return sessionRepository.findBySessionId(sessionId)
                                .flatMap(session -> {
                                        session.setStatus(status);
                                        return sessionRepository.save(session);
                                });
        }
}