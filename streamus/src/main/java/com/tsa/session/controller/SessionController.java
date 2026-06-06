package com.streamus.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.streamus.api.dto.CreateSessionRequest;
import com.streamus.api.dto.CreateSessionResponse;
import com.streamus.session.service.SessionService;

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
}