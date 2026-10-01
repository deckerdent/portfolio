package com.portfolio.portal.controller;

import com.portfolio.portal.api.HostApi;
import com.portfolio.portal.host.HostConfigService;
import com.portfolio.portal.host.mapper.HostConfigMapper;
import com.portfolio.portal.host.model.PortalHost;
import com.portfolio.portal.model.ConfigPropsDto;
import com.portfolio.portal.model.ConfigPropRequest;
import com.portfolio.portal.model.HostResponse;
import com.portfolio.portal.model.InitializedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class HostController implements HostApi {

    private final HostConfigService service;
    private final PortalHost host;
    private final HostConfigMapper mapper;

    @Override
    public Mono<ResponseEntity<HostResponse>> getHost(ServerWebExchange exchange) {
        HostResponse response = new HostResponse();
        response.setTitle(host.getTitle());
        response.setBasePath(host.getBasePath());
        response.setSourceUrls(host.getSourceUrls());
        response.setAppsUrl(host.getAppsUrl());
        response.setConfigProps(host.getConfigProps());
        return Mono.just(ResponseEntity.ok(response));
    }

    @Override
    public Mono<ResponseEntity<Flux<ConfigPropsDto>>> getHostConfig(ServerWebExchange exchange) {
        return service.findAll()
                .map(list -> ResponseEntity.ok(Flux.fromIterable(mapper.toDtoList(list))));
    }

    @Override
    public Mono<ResponseEntity<ConfigPropsDto>> upsertHostConfig(
            Mono<ConfigPropRequest> configPropRequest, ServerWebExchange exchange) {
        return configPropRequest
                .flatMap(req -> service.upsert(req.getKey(), req.getValue()))
                .map(mapper::toDto)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @Override
    public Mono<ResponseEntity<ConfigPropsDto>> updateHostConfigByKey(
            String key, Mono<ConfigPropRequest> configPropRequest, ServerWebExchange exchange) {
        return configPropRequest
                .flatMap(req -> service.update(key, req.getValue()))
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteHostConfigByKey(
            String key, ServerWebExchange exchange) {
        return service.delete(key)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @Override
    public Mono<ResponseEntity<InitializedResponse>> isHostInitialized(ServerWebExchange exchange) {
        return service.isInitialized()
                .map(initialized -> {
                    InitializedResponse response = new InitializedResponse();
                    response.setInitialized(initialized);
                    return ResponseEntity.ok(response);
                });
    }
}
