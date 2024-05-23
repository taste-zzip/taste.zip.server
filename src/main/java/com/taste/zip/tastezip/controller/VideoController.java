package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingCreateRequest;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingCreateResponse;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteRequest;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteResponse;
import com.taste.zip.tastezip.dto.AccountVideoMappingCreateRequest;
import com.taste.zip.tastezip.dto.AccountVideoMappingCreateResponse;
import com.taste.zip.tastezip.dto.AccountVideoMappingDeleteRequest;
import com.taste.zip.tastezip.dto.AccountVideoMappingDeleteResponse;
import com.taste.zip.tastezip.dto.VideoFeedResponse;
import com.taste.zip.tastezip.service.VideoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "영상 피드 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때 / 필수 파라미터를 입력하지 않았을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/video/feed")
    public ResponseEntity<VideoFeedResponse> getVideoFeed(
        @RequestParam(required = true) long size,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final VideoFeedResponse response = videoService.getVideoFeed(size, tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "영상 상호작용 저장")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저/요청의 식당 Id가 존재하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 상호작용일 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/video/account")
    public ResponseEntity<AccountVideoMappingCreateResponse> saveInteraction(
        @Valid @RequestBody AccountVideoMappingCreateRequest request,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final AccountVideoMappingCreateResponse response = videoService.saveInteract(request, tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "영상 상호작용 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 상호작용일 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
    })
    @DeleteMapping("/video/account")
    public ResponseEntity<AccountVideoMappingDeleteResponse> deleteInteraction(
        @Valid @RequestBody AccountVideoMappingDeleteRequest request,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final AccountVideoMappingDeleteResponse response = videoService.deleteInteract(request, tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
