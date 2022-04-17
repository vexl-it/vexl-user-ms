package com.cleevio.vexl.module.file.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileErrorType implements ErrorType {

	FILE_NOT_FOUND("100", "File was not found."),
	FILE_READ("101", "Issue with reading stored file."),
	FILE_WRITE("102", "Issue with store of file."),
	;

	/**
	 * Error custom code
	 */
	private final String code;

	/**
	 * Error custom message
	 */
	private final String message;
}
