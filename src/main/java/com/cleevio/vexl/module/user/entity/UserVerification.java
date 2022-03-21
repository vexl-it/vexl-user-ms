package com.cleevio.vexl.module.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.Column;
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
    @Nullable
    private String verificationCode;

    @Column(name = "phone_number")
    @Nullable
    private byte[] phoneNumber;

    @Column(name = "expiration_at")
    @Nullable
    private ZonedDateTime expirationAt;

    @Column
    @Nullable
    private String challenge;

    @Column(name = "phone_verified")
    private boolean phoneVerified;

	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OneToOne
    private User user;

}
