package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.dto.AccountCreatorRegistrationResponse;
import com.taste.zip.tastezip.dto.AccountDeleteResponse;
import com.taste.zip.tastezip.dto.AccountDetailResponse;
import com.taste.zip.tastezip.dto.AccountUpdateRequest;
import com.taste.zip.tastezip.dto.AccountUpdateResponse;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "내 정보 확인")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/account")
    public ResponseEntity<AccountDetailResponse> findMyAccount(@Parameter(hidden = true) @AccessToken TokenDetail tokenDetail) {
        final AccountDetailResponse response = accountService.findMyAccount(tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "내 정보 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/account")
    public ResponseEntity<AccountUpdateResponse> updateMyAccount(
        @Valid @RequestBody AccountUpdateRequest request,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final AccountUpdateResponse response = accountService.updateMyAccount(request, tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "내 계정 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @DeleteMapping("/account")
    public ResponseEntity<AccountDeleteResponse> deleteMyAccount(@Parameter(hidden = true) @AccessToken TokenDetail tokenDetail) {
        final AccountDeleteResponse response = accountService.deleteMyAccount(tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "계정 크리에이터 등록하기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/account/creator/registration")
    public ResponseEntity<AccountCreatorRegistrationResponse> registerCreator(@Parameter(hidden = true) @AccessToken TokenDetail tokenDetail) {
        final AccountCreatorRegistrationResponse response = accountService.registerCreator(tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
