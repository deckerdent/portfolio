package com.portfolio.portal.controller;

import com.portfolio.portal.api.ApiApi;
import com.portfolio.portal.model.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class HelloController implements ApiApi {

    @Override
    public Mono<ResponseEntity<HelloResponse>> getHello(ServerWebExchange exchange) {
        HelloResponse response = new HelloResponse();
        response.setMessage("Hello World");
        return Mono.just(ResponseEntity.ok(response));
    }
}
