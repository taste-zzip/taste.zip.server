package com.taste.zip.tastezip.auth;

import io.jsonwebtoken.Jwt;
import java.time.Duration;

public interface TokenProvider {

    enum Type {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    String parseToken(String token);

    Object getPayload(String token);

    boolean isExpired(String token);

    String createToken(Duration duration, Type tokenType, Object payload);
}
