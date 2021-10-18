package com.aikitdigital.demoproject.converter.helper;

import com.aikitdigital.demoproject.model.service.QUser;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
public class SearchQueryDefinitionHelper {

	private Class<?> entityType;
	private List<Path<?>> selectors;
	@Singular("validOperatorsForTypeGroup")
	private Map<Class<?>, Set<ComparisonOperator>> validOperators;

	public Optional<Path<?>> getSelectorPath(String selector) {
		return selectors.stream()
				.filter(path -> path.getMetadata().getName().equals(selector))
				.findFirst();
	}

	@SuppressWarnings("rawtypes")
	public Optional<ComparableExpressionBase> getSelectorAsComparableExpressionBase(String selector) {
		return selectors.stream()
				.filter(path -> path.getMetadata().getName().equals(selector))
				.filter(ComparableExpressionBase.class::isInstance)
				.map(ComparableExpressionBase.class::cast)
				.findFirst();
	}

	public static Class<?> typeGroupOf(Path<?> selector) {
		return selector.getClass();
	}
}
