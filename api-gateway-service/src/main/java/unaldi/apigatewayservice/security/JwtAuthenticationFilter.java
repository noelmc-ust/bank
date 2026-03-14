package unaldi.apigatewayservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITELIST = List.of(
            "/api/v1/auth/",   // login, me
            "/actuator/"       // actuator
    );

    private final SecretKey key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        final var request = exchange.getRequest();
        final String path = request.getURI().getPath();

        // ✅ Always allow preflight (CORS) requests
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // Allow whitelisted paths without JWT, and anything outside /api/v1
        if (isWhitelisted(path) || !path.startsWith("/api/v1/")) {
            return chain.filter(exchange);
        }

        // Expect Authorization: Bearer <token>
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            claims = jws.getBody();
        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "Token expired");
        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid token");
        }

        // Extract identity
        String sub = claims.getSubject(); // user id
        String username = claims.get("username", String.class);
        Object rolesObj = claims.get("roles");
        String rolesHeader = toRolesHeader(rolesObj);

        // Mutate request with identity headers for downstream services
        var mutated = request.mutate()
                .header("X-User-Id", sub != null ? sub : "")
                .header("X-User-Username", username != null ? username : "")
                .header("X-User-Roles", rolesHeader)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private String toRolesHeader(Object rolesObj) {
        if (rolesObj == null) return "";
        if (rolesObj instanceof Collection<?> col) {
            return col.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        }
        return String.valueOf(rolesObj);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", exchange.getRequest().getURI().getPath());

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"status\":401,\"error\":\"Unauthorized\"}").getBytes(StandardCharsets.UTF_8);
        }

        var buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // run early
    }
}