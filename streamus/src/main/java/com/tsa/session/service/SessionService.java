package com.tsa.session.service;

import com.tsa.api.dto.CreateSessionRequest;
import com.tsa.api.dto.CreateSessionResponse;
import com.tsa.session.entity.StreamSession;
import com.tsa.session.model.SessionStatus;

import reactor.core.publisher.Mono;

public interface SessionService {

    Mono<CreateSessionResponse> createSession(CreateSessionRequest request);

    Mono<CreateSessionResponse> getSession(String sessionId);

    Mono<StreamSession> updateStatus(String sessionId, SessionStatus status) ;
}