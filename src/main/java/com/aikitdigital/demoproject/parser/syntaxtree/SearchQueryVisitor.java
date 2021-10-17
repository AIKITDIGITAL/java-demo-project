package com.aikitdigital.demoproject.parser.syntaxtree;

public interface SearchQueryVisitor<R, A> {

    R visit(AndNode node, A param);

    R visit(OrNode node, A param);

    R visit(ComparisonNode node, A param);
}
