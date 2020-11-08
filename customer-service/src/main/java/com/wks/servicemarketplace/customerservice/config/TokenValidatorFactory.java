package com.wks.servicemarketplace.customerservice.config;

import com.wks.servicemarketplace.customerservice.adapters.auth.StandardTokenValidator;
import com.wks.servicemarketplace.customerservice.adapters.auth.TokenValidator;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

public class TokenValidatorFactory implements Factory<TokenValidator> {

    private final StandardTokenValidator tokenValidator;

    @Inject
    public TokenValidatorFactory() throws Exception {
        tokenValidator = new StandardTokenValidator(getPublicKey());
    }

    private PublicKey getPublicKey() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("publicKey.pem"));
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            final String content = reader.lines().collect(Collectors.joining(""))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final X509EncodedKeySpec keySpecPKCS8 = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(content));
            return keyFactory.generatePublic(keySpecPKCS8);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    @Override
    public TokenValidator provide() {
        return tokenValidator;
    }

    @Override
    public void dispose(TokenValidator instance) {

    }
}
