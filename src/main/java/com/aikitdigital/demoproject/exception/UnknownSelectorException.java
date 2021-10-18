package com.aikitdigital.demoproject.exception;

public class UnknownSelectorException extends InvalidSearchQueryException {

	public UnknownSelectorException(String selector, Class<?> entityType) {
		super("Unknown SearchQuery[" + entityType + "] selector: " + selector);
	}

}
