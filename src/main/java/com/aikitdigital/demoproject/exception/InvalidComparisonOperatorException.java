package com.aikitdigital.demoproject.exception;

import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;

import java.util.Set;

public class InvalidComparisonOperatorException extends InvalidSearchQueryException {

	public InvalidComparisonOperatorException(ComparisonNode node, Class<?> selectorType, Set<ComparisonOperator> validOperators, Class<?> entityClass) {
		super("Invalid SearchQuery" +
				"[" +
				entityClass +
				"]" +
				" " +
				"comparison operator: '" +
				node.getOperator() +
				"' for selector: '" +
				node.getSelector() +
				"'. Valid operators for the selector type: '" +
				selectorType +
				"' are: " +
				validOperators
		);
	}
}
