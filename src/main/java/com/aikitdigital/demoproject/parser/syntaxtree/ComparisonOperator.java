package com.aikitdigital.demoproject.parser.syntaxtree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ComparisonOperator {
	EQUAL(":"),
	NOT_EQUAL("~"),
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL("<=");

	private final String symbol;

	@Override
	public String toString() {
		return getSymbol();
	}
}
