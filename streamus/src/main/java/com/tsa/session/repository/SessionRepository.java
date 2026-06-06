package com.tsa.session.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.streamus.session.entity.StreamSession;

import reactor.core.publisher.Mono;

public interface SessionRepository
        extends ReactiveCrudRepository<StreamSession, Long> {

    Mono<StreamSession> findBySessionId(String sessionId);

}