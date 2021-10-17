package com.aikitdigital.demoproject.parser;

import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import com.aikitdigital.demoproject.parser.syntaxtree.DefaultNodesFactory;
import com.aikitdigital.demoproject.parser.syntaxtree.LogicalNode;
import com.aikitdigital.demoproject.parser.syntaxtree.LogicalOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SearchQueryParserTest {

	private final SearchQueryParserComponent searchQueryParser = new SearchQueryParserComponent(new DefaultNodesFactory());

	@ParameterizedTest
	@ValueSource(strings = {":", "~", "<", ">", "<=", ">="})
	void shouldParseComparisonExpression(String operator) {
		var selector = "name";
		var argument = "Thanos";

		var parsedQueryNode = searchQueryParser.parse(comparisonExpression(operator, selector, argument)).getNode();

		assertThat(parsedQueryNode).isInstanceOf(ComparisonNode.class);
		var comparisonNode = (ComparisonNode) parsedQueryNode;
		assertComparisonNode(comparisonNode, operator, selector, argument);
	}

	@ParameterizedTest
	@ValueSource(strings = {",", "|"})
	void shouldParseLogicalOperators(String logicalOperator) {
		var selector1 = "name";
		var argument1 = "Thanos";
		var selector2 = "id";
		var argument2 = "5";

		var parsedQueryNode = searchQueryParser.parse(
				comparisonExpression(":", selector1, argument1)
						+ logicalOperator +
						comparisonExpression(">=", selector2, argument2)
		).getNode();

		assertThat(parsedQueryNode).isInstanceOf(LogicalNode.class);
		var logicalNode = (LogicalNode) parsedQueryNode;
		assertThat(logicalNode.getOperator().getSymbol()).isEqualTo(logicalOperator);
		assertComparisonNode((ComparisonNode) logicalNode.getChildren().get(0), ":", selector1, argument1);
		assertComparisonNode((ComparisonNode) logicalNode.getChildren().get(1), ">=", selector2, argument2);
	}

	@Test
	void shouldParseCombinationOfExpressionsWithBracketPrecedence() {
		var selector1 = "name";
		var argument1 = "Thanos";
		var selector2 = "id";
		var argument2a = "5";
		var argument2b = "10";

		var parsedQueryNode = searchQueryParser.parse(
				comparisonExpression(":", selector1, argument1)
						+ "|"
						+ "(" +
									comparisonExpression(">=", selector2, argument2a)
							+ "," + comparisonExpression("<=", selector2, argument2b) +
						")"
		).getNode();

		assertThat(parsedQueryNode).isInstanceOf(LogicalNode.class);
		var root = (LogicalNode) parsedQueryNode;
		assertThat(root.getOperator()).isEqualTo(LogicalOperator.OR);
		assertComparisonNode((ComparisonNode) root.getChildren().get(0), ":", selector1, argument1);

		assertThat(root.getChildren().get(1)).isInstanceOf(LogicalNode.class);
		var logicalNodeChildren = (LogicalNode)root.getChildren().get(1);
		assertThat(logicalNodeChildren.getOperator()).isEqualTo(LogicalOperator.AND);
		assertComparisonNode((ComparisonNode) logicalNodeChildren.getChildren().get(0), ">=", selector2, argument2a);
		assertComparisonNode((ComparisonNode) logicalNodeChildren.getChildren().get(1), "<=", selector2, argument2b);
	}

	@Test
	void shouldParseNullValue() {
		var selector = "name";
		var argument = "null";

		var parsedQueryNode = searchQueryParser.parse(comparisonExpression(":", selector, argument)).getNode();

		assertThat(parsedQueryNode).isInstanceOf(ComparisonNode.class);
		var comparisonNode = (ComparisonNode) parsedQueryNode;
		assertThat(comparisonNode.getOperator()).isEqualTo(ComparisonOperator.EQUAL);
		assertThat(comparisonNode.getSelector()).isEqualTo(selector);
		assertThat(comparisonNode.getArgument()).isEmpty();
	}

	@Test
	void shouldThrowParseExceptionForUnknownOperator() {
		var selector = "name";
		var operator = "==";
		var argument = "Thanos";
		assertThrows(SearchQueryParserException.class, () -> searchQueryParser.parse(comparisonExpression(operator, selector, argument)));
	}

	private void assertComparisonNode(ComparisonNode comparisonNode, String operator, String selector, String argument) {
		assertThat(comparisonNode.getOperator().getSymbol()).isEqualTo(operator);
		assertThat(comparisonNode.getSelector()).isEqualTo(selector);
		assertThat(comparisonNode.getArgument()).hasValue(argument);
	}

	private String comparisonExpression(String operator, String selector, String argument) {
		return selector + operator + argument;
	}
}
