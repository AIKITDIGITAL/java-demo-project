package com.aikitdigital.demoproject.parser.syntaxtree;

import com.aikitdigital.demoproject.parser.UnknownOperatorException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultNodesFactory implements NodesFactory {

	private final Map<String, ComparisonOperator> comparisonOperators;

	public DefaultNodesFactory() {
		this.comparisonOperators = SearchQueryOperators.defaultOperators().stream()
				.collect(Collectors.toMap(
						ComparisonOperator::getSymbol,
						Function.identity()
				));
	}

	@Override
	public LogicalNode createLogicalNode(LogicalOperator operator, List<Node> children) {
		switch (operator) {
			case AND:
				return new AndNode(children);
			case OR:
				return new OrNode(children);
			default:
				throw new IllegalStateException("Unknown operator: " + operator);
		}
	}

	@Override
	public ComparisonNode createComparisonNode(String operatorToken, String selector, Optional<String> argument) throws UnknownOperatorException {
		var comparisonOperator = comparisonOperators.get(operatorToken);
		if (comparisonOperator != null) {
			return new ComparisonNode(comparisonOperator, selector, argument);
		} else {
			throw new UnknownOperatorException(operatorToken);
		}
	}
}
