package me.tuhin47.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tuhin47.apigateway.exception.JwtTokenMalformedException;
import me.tuhin47.apigateway.exception.JwtTokenMissingException;
import me.tuhin47.config.AppProperties;
import me.tuhin47.jwt.TokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Boolean noAuth = appProperties.getConfig().getNoAuth();
        if (noAuth != null && noAuth) {
            log.info("No Auth is active. Ignoring authentication with default token");
            exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, "Bearer " + appProperties.getConfig().getNoAuthToken()).build();
        }
        log.info("JwtAuthenticationFilter | filter is working");

        ServerHttpRequest request = exchange.getRequest();

        final List<String> apiEndpoints = List.of("/signup", "/refreshtoken", "/signin", "/ws", "/v3/api-docs", "/auth/", "/oauth2/");

        Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream().noneMatch(uri -> r.getURI().getPath().contains(uri));

        log.info("JwtAuthenticationFilter | filter | isApiSecured.test(request) : " + isApiSecured.test(request));

        if (isApiSecured.test(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            final String authorization = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
            final String token = authorization.replace("Bearer ", "");

            log.info("JwtAuthenticationFilter | filter | token : " + token);

            try {
                tokenProvider.validateToken(token);
            } catch (ExpiredJwtException e) {
                log.info("JwtAuthenticationFilter | filter | ExpiredJwtException | error : " + e.getMessage());
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();

            } catch (IllegalArgumentException | JwtTokenMalformedException | JwtTokenMissingException
                     | SignatureException | UnsupportedJwtException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);

                return response.setComplete();
            }

            Claims claims = tokenProvider.getClaims(token);
            exchange.getRequest().mutate().header("username", String.valueOf(claims.get("username"))).build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
