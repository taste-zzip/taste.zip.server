package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.auth.TokenDetail;

public record AuthRegistrationResponse(
    TokenDetail tokenDetail,
    String accessToken,
    String refreshToken
) {

}
