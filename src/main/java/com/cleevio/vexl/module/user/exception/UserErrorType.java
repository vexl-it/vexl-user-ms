package com.cleevio.vexl.module.user.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements ErrorType {

    USER_DUPLICATE("101", "User already exists"),
	USER_NOT_FOUND("103", "User not found"),
	VERIFICATION_NOT_FOUND("104", "User does not have verification"),
	SIGNATURE_ERROR("105", "Error occured during creating signature");


	/**
	 * Error custom code
	 */
	private final String code;

	/**
	 * Error custom message
	 */
	private final String message;
}
