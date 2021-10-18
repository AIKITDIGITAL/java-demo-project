
package com.aikitdigital.demoproject.parser.syntaxtree;

import lombok.Data;
import org.springframework.util.Assert;

import java.util.Optional;

@Data
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ComparisonNode extends AbstractNode {

	private final ComparisonOperator operator;
	private final String selector;
	private final Optional<String> argument;

	public ComparisonNode(ComparisonOperator operator, String selector, Optional<String> argument) {
		Assert.notNull(operator, "operator must not be null");
		Assert.hasLength(selector, "selector must not be empty");
		Assert.notNull(argument, "argument must not be null");
		this.operator = operator;
		this.selector = selector;
		this.argument = argument;
	}

	public <R, A> R accept(SearchQueryVisitor<R, A> visitor, A param) {
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		return selector + operator + "'" + argument + "'";
	}
}
