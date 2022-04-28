package com.cleevio.vexl.module.user.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements ErrorType {

    USER_DUPLICATE("101", "User already exists."),
	USER_NOT_FOUND("103", "User not found."),
	VERIFICATION_NOT_FOUND("104", "Verification not found. It can mean verification is already expired."),
	SIGNATURE_ERROR("105", "Error occurred during creating signature."),
	CHALLENGE_ERROR("106", "Error occurred during generating challenge."),
	INVALID_PK_HASH("108", "Server could not create message for signature. Public key or hash is invalid."),
	USERNAME_NOT_AVAILABLE("109", "Username is not available. Choose different username."),
	USER_PHONE_INVALID("110", "Invalid phone number.");

	/**
	 * Error custom code
	 */
	private final String code;

	/**
	 * Error custom message
	 */
	private final String message;
}
