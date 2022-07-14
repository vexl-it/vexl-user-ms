package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.Optional;

interface UserVerificationRepository extends JpaRepository<UserVerification, Long>, JpaSpecificationExecutor<UserVerification> {

    @Query("select uv from user_verification uv where uv.expirationAt > :now AND uv.id = :id AND uv.verificationCode = :code ")
    Optional<UserVerification> findValidUserVerificationByIdAndCode(Long id, String code, ZonedDateTime now);

    @Modifying
    @Query("delete from user_verification uv where uv.expirationAt < :expiration ")
    void deleteExpiredVerifications(ZonedDateTime expiration);
}
