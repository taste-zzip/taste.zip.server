package com.taste.zip.tastezip.auth;

import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface OAuthProvider {

    URI getLoginUri();

    OAuthCredential authorize(String code);

    OAuthType getType();
}
