package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.dto.*;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.service.CafeteriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CafeteriaController {

    private final CafeteriaService cafeteriaService;

    @Operation(summary = "음식점 검색")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/cafeteria/list")
    public ResponseEntity<Page<CafeteriaResponse>> findCafeteriaByKeyword(
        @RequestParam(value = "keyword") String keyword,
        @Parameter(description = "sort는 name,asc처럼 column,direction을 적으면 된다.") Pageable pageable,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        Page<CafeteriaResponse> responses = cafeteriaService.findByKeyword(keyword, pageable, tokenDetail);
        return ResponseEntity.ok(responses);
    }

    /**
     * Temporary API for middle-demo
     */
    @Operation(summary = "좋아요한 모든 음식점 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/cafeteria/like")
    public ResponseEntity<CafeteriaLikeResponse> getCafeteriaLiked(@Parameter(hidden = true) @AccessToken TokenDetail tokenDetail) {
        final CafeteriaLikeResponse response = cafeteriaService.getCafeteriaLiked(tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "음식점 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저가 존재하지 않을 때 / 해당 식당이 존재하지 않을 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/cafeteria/{cafeteriaId}")
    public ResponseEntity<CafeteriaDetailResponse> findById(
        @PathVariable Long cafeteriaId,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        return ResponseEntity.ok(cafeteriaService.getById(cafeteriaId, tokenDetail));
    }

    @Operation(summary = "음식점 상호작용 저장")
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
    @PostMapping("/cafeteria/account")
    public ResponseEntity<AccountCafeteriaMappingCreateResponse> saveInteraction(
        @Valid @RequestBody AccountCafeteriaMappingCreateRequest request,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final AccountCafeteriaMappingCreateResponse response = cafeteriaService.saveInteract(request, tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "음식점 상호작용 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 상호작용일 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
    })
    @DeleteMapping("/cafeteria/account")
    public ResponseEntity<AccountCafeteriaMappingDeleteResponse> deleteInteraction(
        @RequestParam(required = true) Long cafeteriaId,
        @RequestParam(required = true) AccountCafeteriaMappingType type,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final AccountCafeteriaMappingDeleteResponse response = cafeteriaService.deleteInteract(cafeteriaId, type, tokenDetail);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "음식점 댓글 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저 / cafeteriaId에 존재하는 식당이 존재하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/cafeteria/{cafeteriaId}/comment")
    public ResponseEntity<CafeteriaCommentListResponse> readCafeteriaCommentList(
        @PathVariable(required = true) Long cafeteriaId,
        Pageable pageable,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final CafeteriaCommentListResponse response = cafeteriaService.readCafeteriaCommentList(cafeteriaId, pageable, tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "음식점 댓글 생성")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저 / cafeteriaId에 존재하는 식당이 존재하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @PostMapping("/cafeteria/{cafeteriaId}/comment")
    public ResponseEntity<CafeteriaCommentCreateResponse> createCafeteriaComment(
        @Valid @RequestBody CafeteriaCommentCreateRequest request,
        @PathVariable(required = true) Long cafeteriaId,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final CafeteriaCommentCreateResponse response = cafeteriaService.createComment(request, cafeteriaId, tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "음식점 댓글 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "403", description = "Token User가 Comment의 작성자가 아닐 때",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 / 댓글일 경우",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
    })
    @DeleteMapping("/cafeteria/comment/{commentId}")
    public ResponseEntity<CafeteriaCommentDeleteResponse> deleteCafeteriaComment(
        @PathVariable(required = true) Long commentId,
        @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        final CafeteriaCommentDeleteResponse response = cafeteriaService.deleteComment(commentId, tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "추천")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "토큰이 만료/변조/비유효 할 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "401", description = "Authorization Header를 입력하지 않거나 Bearer로 시작하지 않을 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저 / cafeteriaId에 존재하는 식당이 존재하지 않을 때",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    @GetMapping("/cafeteria/recommendations")
    public ResponseEntity<List<CafeteriaDetailResponse>> recommend(
            @Parameter(hidden = true) @AccessToken TokenDetail tokenDetail
    ) {
        List<CafeteriaDetailResponse> response = cafeteriaService.getRecommendations(tokenDetail);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
