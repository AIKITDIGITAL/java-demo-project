package com.aikitdigital.demoproject.converter;

import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.exception.InvalidSortFieldException;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SortToQueryDslOrderSpecifiersConverter {

	@SuppressWarnings("rawtypes")
	public List<OrderSpecifier> convert(Sort sort, SearchQueryDefinitionHelper searchQueryDefinitionHelper) {
		return sort.get()
				.map(order -> {
					var comparableExpression = searchQueryDefinitionHelper.getSelectorAsComparableExpressionBase(order.getProperty())
							.orElseThrow(() -> new InvalidSortFieldException("Invalid order property: " + order.getProperty()));
					return order.getDirection().isAscending() ? comparableExpression.asc() : comparableExpression.desc();
				})
				.collect(Collectors.toList());
	}
}
