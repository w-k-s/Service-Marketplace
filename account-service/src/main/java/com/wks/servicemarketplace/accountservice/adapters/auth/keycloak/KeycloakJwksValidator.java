package com.wks.servicemarketplace.accountservice.adapters.auth.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.accountservice.adapters.auth.InvalidTokenException;
import com.wks.servicemarketplace.accountservice.adapters.auth.TokenValidator;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodeValidator;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class KeycloakJwksValidator implements TokenValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakJwksValidator.class);

    private JwtConsumer consumer;
    private final ObjectMapper objectMapper;

    private KeycloakJwksValidator(final JsonWebKey jsonWebKey, ObjectMapper objectMapper) {
        this.consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(jsonWebKey.getKey())
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build();
        this.objectMapper = objectMapper;
    }

    public static KeycloakJwksValidator withKeySetFromURL(String keySetURLString, ObjectMapper objectMapper) throws JoseException, IOException {
        try {
            LOGGER.info("Loading keySet From Keycloak");
            HttpsJwks httpsJwks = new HttpsJwks(keySetURLString);
            return new KeycloakJwksValidator(httpsJwks.getJsonWebKeys().get(0), objectMapper);
        } catch (Exception e) {
            LOGGER.error("Failed to load keysets from keycloak", e);
            throw e;
        }
    }

    @Override
    public KeycloakUser getUserIfValid(String token) throws InvalidTokenException {
        try {
            final JwtClaims claims = consumer.processToClaims(token);

            return KeycloakUser.builder()
                    .uuid(claims.getSubject())
                    .scope(claims.getClaimValueAsString("scope"))
                    .emailVerified(claims.getClaimValue("email_verified", Boolean.class))
                    .name(claims.getClaimValueAsString("name"))
                    .preferredUsername(claims.getClaimValueAsString("preferred_username"))
                    .givenName(claims.getClaimValueAsString("given_name"))
                    .familyName(claims.getClaimValueAsString("family_name"))
                    .email(claims.getClaimValueAsString("email"))
                    .roles(getRoles(claims))
                    .build();
        } catch (InvalidJwtException e) {
            LOGGER.error("Invalid JWT Token: {}", e.getMessage(), e);

            final String message = e.getErrorDetails()
                    .stream()
                    .map(ErrorCodeValidator.Error::getErrorMessage)
                    .collect(Collectors.joining(","));

            throw new InvalidTokenException(message, e);
        } catch (MalformedClaimException e) {
            LOGGER.error("Invalid Claim: {}", e.getMessage(), e);
            throw new InvalidTokenException("Token's claims could not be read", e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to process roles: {}", e.getMessage(), e);
            throw new InvalidTokenException("Token's roles could not be read", e);
        }
    }

    private List<String> getRoles(JwtClaims claims) throws JsonProcessingException {
        final String json = objectMapper.writeValueAsString(claims.getClaimsMap());
        final ClaimsStruct claimsStruct = objectMapper.readValue(json, ClaimsStruct.class);
        return claimsStruct.getRoles();
    }
}
