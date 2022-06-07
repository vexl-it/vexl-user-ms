package com.cleevio.vexl.module.user.entity;

import com.cleevio.vexl.common.convertor.AesEncryptionConvertor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.ZonedDateTime;

@Entity(name = "user_verification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_code")
    @Convert(converter = AesEncryptionConvertor.class)
    private String verificationCode;

    @Column(name = "phone_number")
    private byte[] phoneNumber;

    @Column(name = "expiration_at")
    private ZonedDateTime expirationAt;

    @Column
    @Convert(converter = AesEncryptionConvertor.class)
    private String challenge;

    @Column(name = "phone_verified")
    private boolean phoneVerified;

	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OneToOne
    private User user;

}
