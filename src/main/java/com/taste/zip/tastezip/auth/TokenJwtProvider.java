package com.taste.zip.tastezip.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.lang.reflect.Field;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;

public class TokenJwtProvider implements TokenProvider {

    private final Key secretKey;
    private final Class<?> payloadClass;


    public TokenJwtProvider(String secretKey, Class<?> payloadClass) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        this.payloadClass = payloadClass;
    }

    @Override
    public String parseToken(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        final Jwt<?, ?> jwt = parser.parse(token);

        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            stringBuilder.append("{ \"header\": ");
            final String header = objectMapper.writeValueAsString(jwt.getHeader());
            stringBuilder.append(header);

            stringBuilder.append(", \"body\": ");
            final String body = objectMapper.writeValueAsString(jwt.getBody());
            stringBuilder.append(body);
            stringBuilder.append("}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }

    @Override
    public Object getPayload(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {

        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        return payloadClass.cast(parser.parseClaimsJws(token).getBody());
    }

    @Override
    public boolean isExpired(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return true;
        }

        return false;
    }

    @Override
    public String createToken(Duration duration, Type tokenType, Object payload) {
        assert payload.getClass() == this.payloadClass;
        return createJwt(duration, tokenType, payload);
    }

    @SneakyThrows
    private String createJwt(Duration duration, Type tokenType, Object payload) {
        Map<String, String> claims = new HashMap<>();

        Field[] fields = payloadClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(payload);
            claims.put(field.getName(), String.valueOf(value));
        }

        return Jwts.builder()
            .setClaims(claims)
            .setHeaderParam("type", tokenType.name())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
            .signWith(secretKey)
            .compact();
    }
}
