package com.wks.servicemarketplace.customerservice.adapters.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wks.servicemarketplace.customerservice.core.auth.Authentication;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodeValidator;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.stream.Collectors;

public class StandardTokenValidator implements TokenValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardTokenValidator.class);

    private final JwtConsumer consumer;
    private final ObjectMapper objectMapper;

    public StandardTokenValidator(PublicKey publicKey,
                                  ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(publicKey)
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build();
    }

    @Override
    public Authentication authenticate(String token) throws InvalidTokenException {
        try {
            final JwtClaims claims = consumer.processToClaims(token);

            return DefaultAuthentication.builder()
                    .user(objectMapper.readValue(claims.getStringClaimValue("user"), DefaultUser.class))
                    .name(claims.getSubject())
                    .permissions(claims.getStringListClaimValue("permissions"))
                    .build();
        } catch (InvalidJwtException e) {
            LOGGER.error("Invalid JWT Token: {}", e.getMessage(), e);

            final String message = e.getErrorDetails()
                    .stream()
                    .map(ErrorCodeValidator.Error::getErrorMessage)
                    .collect(Collectors.joining(","));

            throw new InvalidTokenException(message, e);
        } catch (MalformedClaimException | JsonProcessingException e) {
            LOGGER.error("Invalid Claim: {}", e.getMessage(), e);
            throw new InvalidTokenException("Token's claims could not be read", e);
        }
    }
}
