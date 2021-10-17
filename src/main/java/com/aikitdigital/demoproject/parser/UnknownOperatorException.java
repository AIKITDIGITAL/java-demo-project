package com.aikitdigital.demoproject.parser;

import lombok.Getter;

@Getter
public class UnknownOperatorException extends ParseException {

    private final String operator;

    public UnknownOperatorException(String operator) {
        this(operator, "Unknown operator: " + operator);
    }

    public UnknownOperatorException(String operator, String message) {
        super(message);
        this.operator = operator;
    }
}
