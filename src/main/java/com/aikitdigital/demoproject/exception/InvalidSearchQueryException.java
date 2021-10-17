package com.aikitdigital.demoproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSearchQueryException extends RuntimeException {

	public InvalidSearchQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSearchQueryException(String message) {
		super(message);
	}
}