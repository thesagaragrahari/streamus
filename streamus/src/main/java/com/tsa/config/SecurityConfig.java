package com.tsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

// @Configuration
// public class SecurityConfig {

//     @Bean
//     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http
//                 .csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers(
//                                 "/health",
//                                 "/actuator/**",
//                                 "/swagger-ui/**",
//                                 "/v3/api-docs/**"
//                         ).permitAll()
//                         .anyRequest().authenticated()
//                 )
//                 .httpBasic(Customizer.withDefaults());

//         return http.build();
//     }
// }


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .build();
    }
}