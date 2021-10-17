package com.aikitdigital.demoproject.parser.syntaxtree;

import com.aikitdigital.demoproject.parser.UnknownOperatorException;

import java.util.List;
import java.util.Optional;

public interface NodesFactory {

	LogicalNode createLogicalNode(LogicalOperator operator, List<Node> children);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	ComparisonNode createComparisonNode(String operatorToken, String selector, Optional<String> argument) throws UnknownOperatorException;
}
