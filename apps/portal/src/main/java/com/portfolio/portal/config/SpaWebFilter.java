package com.portfolio.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;

/**
 * Forwards all non-API, non-asset requests to index.html so the
 * client-side Navigo router handles path-based navigation.
 *
 * Without this, navigating directly to e.g. /Default or refreshing a
 * deep-link causes Spring Boot to return 404 instead of serving the SPA.
 *
 * The passthrough pattern is configurable via {@code spa.passthrough-pattern}.
 */
@Component
@Order(-1)
public class SpaWebFilter implements WebFilter {

    private final Pattern passthroughPattern;

    public SpaWebFilter(@Value("${spa.passthrough-pattern}") String passthroughPattern) {
        this.passthroughPattern = Pattern.compile(passthroughPattern);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (passthroughPattern.matcher(path).matches()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest mutated = exchange.getRequest()
                .mutate()
                .path("/index.html")
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }
}
