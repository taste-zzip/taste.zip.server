package com.taste.zip.tastezip.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import jakarta.servlet.DispatcherType;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain restApiRules(HttpSecurity http) throws Exception {
        http
            .securityMatcher(antMatcher("/api/**"))
            .authorizeHttpRequests((authorize) -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers(antMatcher("/health/**")).permitAll()
                .anyRequest().denyAll()
            )
            .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}
