package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenProvider;
import com.taste.zip.tastezip.auth.TokenProvider.Type;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.auth.annotation.AccessTokenResolver;
import com.taste.zip.tastezip.dto.AuthRegistrationRequest;
import com.taste.zip.tastezip.dto.AuthRegistrationResponse;
import com.taste.zip.tastezip.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final TokenProvider tokenProvider;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String id) {
        final String token = tokenProvider.createToken(Duration.ofDays(1), Type.ACCESS_TOKEN,
            TokenDetail.builder(Long.valueOf(id)).build());
        return new ResponseEntity<>(token, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthRegistrationResponse> registration(@Valid @RequestBody AuthRegistrationRequest request) {
        final AuthRegistrationResponse response = accountService.register(request);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(200));
    }

    @GetMapping("/introspect")
    public ResponseEntity<Object> introspectToken(HttpServletRequest request) {
        final String parsedToken = accountService.parseToken(request);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(parsedToken, headers ,HttpStatusCode.valueOf(200));
    }
}
