package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Account;

public record AccountCreatorRegistrationResponse(
    Account account
) {

}
