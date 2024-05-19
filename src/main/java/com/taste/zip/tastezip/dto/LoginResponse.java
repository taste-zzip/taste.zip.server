package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.auth.TokenDetail;

public record LoginResponse(
    TokenDetail tokenDetail,
    String accessToken,
    String refreshToken
) {

}
