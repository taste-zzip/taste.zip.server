package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.dto.YoutubeLikeCafeteriaUpdateRequest;
import com.taste.zip.tastezip.dto.YoutubeLikeCafeteriaUpdateResponse;
import com.taste.zip.tastezip.service.YoutubeService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @Operation(summary = "최근 좋아요 누른 Youtube 영상에 해당하는 Cafeteria Like 생성하기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "유저/유저-인증정보가 없을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/youtube/like-cafeteria-update")
    public ResponseEntity<YoutubeLikeCafeteriaUpdateResponse> updateYoutubeLike(
        @Valid @RequestBody YoutubeLikeCafeteriaUpdateRequest request,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final YoutubeLikeCafeteriaUpdateResponse response = youtubeService.updateYoutubeLike(request, tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
