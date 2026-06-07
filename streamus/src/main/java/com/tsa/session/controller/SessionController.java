package com.tsa.session.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tsa.api.dto.CreateSessionRequest;
import com.tsa.api.dto.CreateSessionResponse;
import com.tsa.session.service.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request) {

        return sessionService.createSession(request);
    }

    @GetMapping("/{sessionId}")
    public Mono<CreateSessionResponse> getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }

}