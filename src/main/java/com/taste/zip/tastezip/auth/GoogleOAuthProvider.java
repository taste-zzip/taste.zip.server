package com.taste.zip.tastezip.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2.Builder;
import com.google.api.services.oauth2.model.Userinfo;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * Contains methods for authorizing a user and caching credentials.
 * <a href="https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/Auth.java">...</a>
 */
@Log4j2
public class GoogleOAuthProvider implements OAuthProvider {

    private final HttpTransport httpTransport;
    /**
     * Define a global instance of the JSON factory.
     */
    private final JsonFactory jsonFactory;

    /**
     * secret key location
     */
    private final String secretLocation;

    private final URI callbackUri;

    private final GoogleAuthorizationCodeFlow flow;

    public GoogleOAuthProvider(List<String> scopes, String secretLocation, URI callbackUri) {
        this.httpTransport = new NetHttpTransport();
        this.jsonFactory = new GsonFactory();

        this.secretLocation = secretLocation;
        this.callbackUri = callbackUri;

        this.flow = new GoogleAuthorizationCodeFlow
            .Builder(httpTransport, jsonFactory, loadSecret(), scopes)
            .setAccessType("offline")
            .build();
    }

    @Override
    public URI getLoginUri() {
        try {
            return new URI(
                flow
                    .newAuthorizationUrl()
                    .setRedirectUri(callbackUri.toString())
                    .build()
            );
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public OAuthType getType() {
        return OAuthType.GOOGLE;
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param code authorization code received after finishing OAuth2.0 login
     */
    @Override
    public OAuthCredential authorize(String code) {
        TokenResponse response = null;
        Credential credential = null;
        Userinfo userinfo = null;

        try {
            response = flow.newTokenRequest(code)
                .setRedirectUri(callbackUri.toString())
                .execute();
            credential = flow.createAndStoreCredential(response, null);
            userinfo = new Builder(httpTransport, jsonFactory, credential).build().userinfo().get()
                .execute();

            return new OAuthCredential(
                OAuthCredential.Token.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .expireSeconds(response.getExpiresInSeconds())
                    .tokenType(response.getTokenType())
                    .scope(response.getScope())
                    .build(),
                OAuthCredential.User.builder(userinfo.getId())
                    .email(userinfo.getEmail())
                    .profileImage(userinfo.getPicture())
                    .rawData(userinfo.toPrettyString())
                    .build()
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private GoogleClientSecrets loadSecret() {
        // Load client secrets.
        Reader clientSecretReader = new InputStreamReader(GoogleOAuthProvider.class.getResourceAsStream(secretLocation));
        GoogleClientSecrets clientSecrets = null;

        // TODO messages.properties로 이동하기
        String errorMessage = "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential into " + secretLocation;
        try {
            clientSecrets = GoogleClientSecrets.load(jsonFactory, clientSecretReader);
        } catch (IOException e) {
            throw new IllegalStateException(errorMessage);
        }

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            log.info(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        return clientSecrets;
    }
}
