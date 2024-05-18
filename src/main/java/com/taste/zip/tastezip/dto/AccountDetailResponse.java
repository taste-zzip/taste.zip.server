package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountConfig;
import com.taste.zip.tastezip.entity.AccountOAuth;
import java.util.List;
import lombok.Builder;

@Builder(builderMethodName = "hiddenBuilder")
public record AccountDetailResponse(
    Account account,
    @JsonIgnoreProperties(value = { "account" })
    List<AccountOAuth> oauthList,
    @JsonIgnoreProperties(value = { "account" })
    List<AccountConfig> configList
) {

    public static AccountDetailResponseBuilder builder(Account account, List<AccountOAuth> oauthList, List<AccountConfig> configList) {
        return hiddenBuilder()
            .account(account)
            .oauthList(oauthList)
            .configList(configList);
    }
}
