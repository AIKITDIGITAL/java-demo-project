package com.aikitdigital.demoproject.parser.syntaxtree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LogicalOperator {
    AND (","),
    OR  ("|");

    private final String symbol;

    @Override
    public String toString() {
        return symbol;
    }
}
