package com.taste.zip.tastezip.config;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.common.collect.Lists;
import com.taste.zip.tastezip.auth.GoogleOAuthProvider;
import com.taste.zip.tastezip.auth.OAuthProvider;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenJwtProvider;
import com.taste.zip.tastezip.auth.TokenProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${authentication.private-key.seed}")
    private String authSeed;

    @Value("${authentication.oauth.secret-path.google}")
    private String googleSecretPath;

    @Value("${authentication.oauth.redirect-uri}")
    private String callbackUri;

    @Bean
    @Order(1)
    SecurityFilterChain restApiRules(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    TokenProvider tokenProvider() {
        return new TokenJwtProvider(authSeed, TokenDetail.class);
    }

    @Bean
    GoogleOAuthProvider googleOAuthProvider() {
        /**
         * TODO type-safe scope 정의하기
         */
        List<String> scopes = List.of(
            "https://www.googleapis.com/auth/youtube",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
        );
        URI callback = null;
        try {
            callback = new URI(callbackUri);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        return new GoogleOAuthProvider(scopes, googleSecretPath, callback);
    }

    /**
     * com.taste.zip.tastezip.entity.enumeration.OAuthType에 정의된 provider들을 등록할 수 있음
     */
    @Bean
    List<OAuthProvider> oAuthProviderList(GoogleOAuthProvider googleOAuthProvider) {
        final List<OAuthProvider> arrayList = new ArrayList<>();

        arrayList.add(googleOAuthProvider);

        // TODO 릴스도 확장되면 instagram도 추가하기

        return arrayList;
    }
}
