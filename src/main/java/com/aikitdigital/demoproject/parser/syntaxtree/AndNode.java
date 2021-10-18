package com.aikitdigital.demoproject.parser.syntaxtree;

import java.util.List;

public final class AndNode extends LogicalNode {

    public AndNode(List<? extends Node> children) {
        super(LogicalOperator.AND, children);
    }

    public <R, A> R accept(SearchQueryVisitor<R, A> visitor, A param) {
        return visitor.visit(this, param);
    }
}
