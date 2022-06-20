package com.cleevio.vexl.common.convertor;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@RequiredArgsConstructor
public class AesEncryptionConvertor implements AttributeConverter<String, String> {

	private final SecretKeyConfig secretKey;

    @Override
    public String convertToDatabaseColumn(String value) {
        if (value == null) return null;
        return CLibrary.CRYPTO_LIB.aes_encrypt(secretKey.aesKey(), value);
    }

    @Override
    public String convertToEntityAttribute(String value) {
        if (value == null) return null;
        return CLibrary.CRYPTO_LIB.aes_decrypt(secretKey.aesKey(), value);
    }
}
