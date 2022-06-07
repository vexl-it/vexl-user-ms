package com.cleevio.vexl.common.convertor;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AesEncryptionConvertor implements AttributeConverter<String, String> {

	@Value("${security.aes.key}")
	protected String key;

    @Override
    public String convertToDatabaseColumn(String value) {
        return CLibrary.CRYPTO_LIB.aes_encrypt(key, value);
    }

    @Override
    public String convertToEntityAttribute(String value) {
        return CLibrary.CRYPTO_LIB.aes_decrypt(key, value);
    }
}
