package com.taste.zip.tastezip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenProvider;
import com.taste.zip.tastezip.auth.TokenProvider.Type;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.auth.annotation.AccessTokenResolver;
import com.taste.zip.tastezip.dto.AuthRegistrationRequest;
import com.taste.zip.tastezip.dto.AuthRegistrationResponse;
import com.taste.zip.tastezip.service.AccountService;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.ErrorResponse;
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

    @Operation(summary = "회원가입 (비인가)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "필수 값을 입력하지 않았을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "409", description = "1. 중복 회원가입을 했을 때\n2. Access/RefreshTokenDuration이 서버에서 설정되있지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/registration")
    public ResponseEntity<AuthRegistrationResponse> registration(@Valid @RequestBody AuthRegistrationRequest request) {
        final AuthRegistrationResponse response = accountService.register(request);
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(200));
    }

    @Operation(summary = "토큰 정보 확인")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = io.jsonwebtoken.Jwt.class)) }),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/introspect")
    public ResponseEntity<Object> introspectToken(HttpServletRequest request) {
        final String parsedToken = accountService.parseToken(request);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(parsedToken, headers ,HttpStatusCode.valueOf(200));
    }
}
