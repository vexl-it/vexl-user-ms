package com.cleevio.vexl.common.convertor;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.user.config.SecretKeyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@RequiredArgsConstructor
public class AesEncryptionConvertor implements AttributeConverter<String, String> {

	private final SecretKeyConfig secretKey;

    @Override
    @Nullable
    public String convertToDatabaseColumn(@Nullable String value) {
        if (value == null) return null;
        return CLibrary.CRYPTO_LIB.aes_encrypt(secretKey.aesKey(), value);
    }

    @Override
    @Nullable
    public String convertToEntityAttribute(@Nullable String value) {
        if (value == null) return null;
        return CLibrary.CRYPTO_LIB.aes_decrypt(secretKey.aesKey(), value);
    }
}
