package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.auth.TokenDetail;
import jakarta.validation.Valid;

@Valid
public record AuthRegistrationResponse(
    TokenDetail tokenDetail,
    String accessToken,
    String refreshToken
) {

}
