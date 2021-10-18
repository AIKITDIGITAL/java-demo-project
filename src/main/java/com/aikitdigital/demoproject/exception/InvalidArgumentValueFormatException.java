package com.aikitdigital.demoproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidArgumentValueFormatException extends InvalidSearchQueryException {

	public InvalidArgumentValueFormatException(String selector, String argument, Class<?> targetType, Class<?> entityType, Throwable throwable) {
		super("Cannot convert SearchQuery[" + entityType + "] " +
				"selector[" + selector + "] argument value: " + argument +
				" to type: " + targetType, throwable);
	}

}
