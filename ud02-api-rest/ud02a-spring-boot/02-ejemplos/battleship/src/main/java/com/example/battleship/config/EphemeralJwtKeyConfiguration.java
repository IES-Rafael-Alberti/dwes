package com.example.battleship.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod & !docker")
public class EphemeralJwtKeyConfiguration {

    @Bean
    KeyPair jwtKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    @Bean
    PrivateKey jwtPrivateKey(KeyPair jwtKeyPair) {
        return jwtKeyPair.getPrivate();
    }

    @Bean
    PublicKey jwtPublicKey(KeyPair jwtKeyPair) {
        return jwtKeyPair.getPublic();
    }
}
