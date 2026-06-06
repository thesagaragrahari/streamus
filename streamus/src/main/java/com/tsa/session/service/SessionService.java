package com.tsa.session.service;

import com.tsa.api.dto.CreateSessionRequest;
import com.tsa.api.dto.CreateSessionResponse;

import reactor.core.publisher.Mono;

public interface SessionService {

    Mono<CreateSessionResponse> createSession(CreateSessionRequest request);

}