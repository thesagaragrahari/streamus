package com.streamus.session.service;

import com.streamus.api.dto.CreateSessionRequest;
import com.streamus.api.dto.CreateSessionResponse;

import reactor.core.publisher.Mono;

public interface SessionService {

    Mono<CreateSessionResponse> createSession(CreateSessionRequest request);

}