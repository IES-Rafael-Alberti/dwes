package com.example.battleship.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration
@Profile({"prod", "docker"})
public class ExternalJwtKeyConfiguration {

    @Bean
    PrivateKey jwtPrivateKey(@Value("${app.security.jwt.private-key}") Resource resource) throws Exception {
        String pem = readPem(resource, "PRIVATE KEY");
        return KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
    }

    @Bean
    PublicKey jwtPublicKey(@Value("${app.security.jwt.public-key}") Resource resource) throws Exception {
        String pem = readPem(resource, "PUBLIC KEY");
        return KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
    }

    private String readPem(Resource resource, String type) throws Exception {
        try (InputStream input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.US_ASCII)
                    .replace("-----BEGIN " + type + "-----", "")
                    .replace("-----END " + type + "-----", "")
                    .replaceAll("\\s", "");
        }
    }
}
