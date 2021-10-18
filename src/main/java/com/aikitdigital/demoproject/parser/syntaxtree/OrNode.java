package com.aikitdigital.demoproject.parser.syntaxtree;

import java.util.List;

public final class OrNode extends LogicalNode {

	public OrNode(List<? extends Node> children) {
		super(LogicalOperator.OR, children);
	}

	public <R, A> R accept(SearchQueryVisitor<R, A> visitor, A param) {
		return visitor.visit(this, param);
	}
}
