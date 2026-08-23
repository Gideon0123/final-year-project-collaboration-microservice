package com.example.COLLABORATION_SERVICE.security;

import com.example.COLLABORATION_SERVICE.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollaborationJwtService {

    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {

        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.getSecret()
        );

        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public Long extractUserId(String token) {

        Object value = extractAllClaims(token)
                .get("userId");

        if (value == null) {

            throw new InvalidTokenException(
                    "JWT does not contain userId"
            );
        }

        return ((Number) value).longValue();
    }

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    public String extractTokenType(String token) {

        return extractAllClaims(token)
                .get("tokenType", String.class);
    }

    public boolean isBlacklisted(String token) {

        Boolean exists = redisTemplate.hasKey(
                "blacklist:" + token
        );

        return Boolean.TRUE.equals(exists);
    }

    public boolean validateAccessToken(String token) {

        try {

            Claims claims = extractAllClaims(token);

            Date expiration = claims.getExpiration();

            if (
                    expiration == null
                            || expiration.before(new Date())
            ) {

                return false;
            }

            String tokenType =
                    claims.get("tokenType", String.class);

            if (!"ACCESS".equals(tokenType)) {

                return false;
            }

            return !isBlacklisted(token);

        } catch (Exception ex) {

            log.warn(
                    "WebSocket JWT validation failed",
                    ex
            );

            return false;
        }
    }
}