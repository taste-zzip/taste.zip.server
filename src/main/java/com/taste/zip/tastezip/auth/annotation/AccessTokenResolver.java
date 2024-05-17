package com.taste.zip.tastezip.auth.annotation;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccessTokenResolver implements HandlerMethodArgumentResolver {

    public static final String X_ACCESS_AUTH = "X-ACCESS-AUTH";
    public static final String X_REFRESH_AUTH = "X-REFRESH-AUTH";
    public static final String BEARER = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAccessToken = parameter.hasParameterAnnotation(AccessToken.class);
        boolean isTokenDetailType = TokenDetail.class.isAssignableFrom(parameter.getParameterType());

        return hasAccessToken && isTokenDetailType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String accessHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessHeader == null || !accessHeader.startsWith(BEARER)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인 해라");
        }

        String token = accessHeader.substring(BEARER.length());
        TokenDetail tokenDetail = null;
        try {
            tokenDetail = (TokenDetail) tokenProvider.getPayload(token);
        } catch (ExpiredJwtException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");
        } catch (MalformedJwtException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 변조되었습니다.");
        } catch (SignatureException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰의 서명이 이상합니다.");
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 null이거나 이상한 값입니다.");
        }

        log.info("Authenticated: " + tokenDetail.userId());
        return tokenDetail;
    }
}
