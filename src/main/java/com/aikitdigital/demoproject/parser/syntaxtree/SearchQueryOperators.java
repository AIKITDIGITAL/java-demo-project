package com.aikitdigital.demoproject.parser.syntaxtree;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchQueryOperators {

	public static Set<ComparisonOperator> defaultOperators() {
		return Arrays.stream(ComparisonOperator.values()).collect(Collectors.toSet());
	}
}
