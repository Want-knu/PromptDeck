package org.example.promtdeck.domain.provider.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyCrypto {

    private final TextEncryptor textEncryptor;

    public ApiKeyCrypto(
            @Value("${provider.api-key-password}") String password,
            @Value("${provider.api-key-salt}") String salt
    ) {
        this.textEncryptor = Encryptors.delux(password, salt);
    }

    public String encrypt(String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }
}
