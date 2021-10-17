package com.aikitdigital.demoproject.converter;

import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.exception.InvalidArgumentValueFormatException;
import com.aikitdigital.demoproject.exception.InvalidComparisonOperatorException;
import com.aikitdigital.demoproject.exception.InvalidSearchQueryException;
import com.aikitdigital.demoproject.exception.UnknownSelectorException;
import com.aikitdigital.demoproject.parser.syntaxtree.AndNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import com.aikitdigital.demoproject.parser.syntaxtree.Node;
import com.aikitdigital.demoproject.parser.syntaxtree.OrNode;
import com.aikitdigital.demoproject.parser.syntaxtree.SearchQueryVisitor;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchQueryToQueryDslConverter implements SearchQueryVisitor<Predicate, SearchQueryDefinitionHelper> {

	private final ConversionService conversionService;

	@Override
	public Predicate visit(AndNode node, SearchQueryDefinitionHelper definitionHelper) {
		return processNodes(node.getChildren(), definitionHelper).stream()
				.reduce(new BooleanBuilder(), BooleanBuilder::and, BooleanBuilder::and);
	}

	@Override
	public Predicate visit(OrNode node, SearchQueryDefinitionHelper definitionHelper) {
		return processNodes(node.getChildren(), definitionHelper).stream()
				.reduce(new BooleanBuilder(), BooleanBuilder::or, BooleanBuilder::or);
	}

	@Override
	public Predicate visit(ComparisonNode node, SearchQueryDefinitionHelper definitionHelper) {
		var selectorPath = getSelectorPathOrThrow(node, definitionHelper);
		validateComparisonOperatorForSelectorOrThrow(selectorPath, node, definitionHelper);
		var convertedArgumentOptional = node.getArgument()
				.map(argument -> convertArgumentOrThrow(node.getSelector(), argument, selectorPath.getType(), definitionHelper.getEntityType()));
		switch (node.getOperator()) {
			case EQUAL:
				return simpleExpressionEqual(selectorPath, convertedArgumentOptional);
			case NOT_EQUAL:
				return simpleExpressionNotEqual(selectorPath, convertedArgumentOptional);
		}

		var convertedArgument = convertedArgumentOptional.orElseThrow(() -> new InvalidSearchQueryException("Invalid operand value 'null' for comparison operator: " + node.getOperator()));
		switch (node.getOperator()) {
			case LESS_THAN:
				return comparableOrNumberExpressionLessThan(selectorPath, convertedArgument);
			case GREATER_THAN:
				return comparableOrNumberExpressionGreaterThan(selectorPath, convertedArgument);
			case LESS_THAN_OR_EQUAL:
				return comparableOrNumberExpressionLessThanOrEqual(selectorPath, convertedArgument);
			case GREATER_THAN_OR_EQUAL:
				return comparableOrNumberExpressionGreaterThanOrEqual(selectorPath, convertedArgument);
			default:
				throw new IllegalStateException("Unknown operator " + node.getOperator());
		}
	}

	@SuppressWarnings({"unchecked"})
	private Predicate comparableOrNumberExpressionLessThan(Path<?> selectorPath, Object convertedArgument) {
		return doOnComparableOrNumberExpression(selectorPath,
				numberExpression -> numberExpression.lt(ConstantImpl.create(convertedArgument)),
				comparableExpression -> comparableExpression.lt(ConstantImpl.create(convertedArgument))
		);
	}

	@SuppressWarnings({"unchecked"})
	private Predicate comparableOrNumberExpressionGreaterThan(Path<?> selectorPath, Object convertedArgument) {
		return doOnComparableOrNumberExpression(selectorPath,
				numberExpression -> numberExpression.gt(ConstantImpl.create(convertedArgument)),
				comparableExpression -> comparableExpression.gt(ConstantImpl.create(convertedArgument))
		);
	}

	@SuppressWarnings({"unchecked"})
	private Predicate comparableOrNumberExpressionLessThanOrEqual(Path<?> selectorPath, Object convertedArgument) {
		return doOnComparableOrNumberExpression(selectorPath,
				numberExpression -> numberExpression.loe(ConstantImpl.create(convertedArgument)),
				comparableExpression -> comparableExpression.loe(ConstantImpl.create(convertedArgument))
		);
	}

	@SuppressWarnings({"unchecked"})
	private Predicate comparableOrNumberExpressionGreaterThanOrEqual(Path<?> selectorPath, Object convertedArgument) {
		return doOnComparableOrNumberExpression(selectorPath,
				numberExpression -> numberExpression.goe(ConstantImpl.create(convertedArgument)),
				comparableExpression -> comparableExpression.goe(ConstantImpl.create(convertedArgument))
		);
	}

	@SuppressWarnings({"rawtypes"})
	private Predicate doOnComparableOrNumberExpression(Path<?> selectorPath,
	                                                   Function<NumberExpression, Predicate> onNumberExpression,
	                                                   Function<ComparableExpression, Predicate> onComparableExpression) {
		if (selectorPath instanceof NumberExpression) {
			return onNumberExpression.apply(asNumberExpression(selectorPath));
		}

		if (selectorPath instanceof ComparableExpression) {
			return onComparableExpression.apply(asComparableExpression(selectorPath));
		}

		//cannot happen
		throw new IllegalStateException("path is not NumberExpression nor ComparableExpression: " + selectorPath);
	}

	@SuppressWarnings({"rawtypes"})
	private NumberExpression asNumberExpression(Path<?> selectorPath) {
		return (NumberExpression) selectorPath;
	}

	@SuppressWarnings({"rawtypes"})
	private ComparableExpression asComparableExpression(Path<?> selectorPath) {
		return (ComparableExpression) selectorPath;
	}

	private void validateComparisonOperatorForSelectorOrThrow(Path<?> selectorPath, ComparisonNode node, SearchQueryDefinitionHelper definitionHelper) {
		if (!isComparisonOperatorValidForSelector(selectorPath, node.getOperator(), definitionHelper)) {
			throw new InvalidComparisonOperatorException(node, selectorPath.getType(),
					definitionHelper.getValidOperators().get(selectorPath.getClass()),
					definitionHelper.getEntityType());
		}
	}

	private boolean isComparisonOperatorValidForSelector(Path<?> selectorPath, ComparisonOperator operator, SearchQueryDefinitionHelper definitionHelper) {
		return definitionHelper.getValidOperators()
				.getOrDefault(selectorPath.getClass(), Set.of())
				.contains(operator);
	}

	private Path<?> getSelectorPathOrThrow(ComparisonNode node, SearchQueryDefinitionHelper definitionHelper) {
		return definitionHelper.getSelectorPath(node.getSelector())
				.orElseThrow(() -> new UnknownSelectorException(node.getSelector(), definitionHelper.getEntityType()));
	}

	private Object convertArgumentOrThrow(String selector, String argument, Class<?> targetType, Class<?> entityType) {
		try {
			return conversionService.convert(argument, targetType);
		} catch (Throwable throwable) {
			throw new InvalidArgumentValueFormatException(
					selector, argument,
					targetType, entityType, throwable);
		}
	}

	private List<Predicate> processNodes(List<Node> nodes, SearchQueryDefinitionHelper definitionHelper) {
		return nodes.stream()
				.map(node -> node.accept(this, definitionHelper))
				.collect(Collectors.toList());
	}

	@SuppressWarnings({"unchecked"})
	private Predicate simpleExpressionNotEqual(Path<?> selectorPath, Optional<Object> optionalValue) {
		var expression = asSimpleExpression(selectorPath);
		return optionalValue.map(expression::ne)
				.orElseGet(expression::isNotNull);
	}

	@SuppressWarnings({"unchecked"})
	private Predicate simpleExpressionEqual(Path<?> selectorPath, Optional<Object> optionalValue) {
		var expression = asSimpleExpression(selectorPath);
		return optionalValue.map(expression::eq)
				.orElseGet(expression::isNull);
	}

	@SuppressWarnings({"rawtypes"})
	private SimpleExpression asSimpleExpression(Path<?> path) {
		if (path instanceof SimpleExpression) {
			return (SimpleExpression) path;
		} else {
			throw new IllegalStateException("path is not SimpleExpression: " + path);
		}

	}
}
