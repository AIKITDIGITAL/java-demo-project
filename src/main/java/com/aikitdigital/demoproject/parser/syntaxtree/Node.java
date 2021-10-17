package com.aikitdigital.demoproject.parser.syntaxtree;

public interface Node {

	<R, A> R accept(SearchQueryVisitor<R, A> visitor, A param);
}
