package com.aikitdigital.demoproject.converter;

import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.exception.InvalidArgumentValueFormatException;
import com.aikitdigital.demoproject.exception.InvalidComparisonOperatorException;
import com.aikitdigital.demoproject.exception.InvalidSearchQueryException;
import com.aikitdigital.demoproject.exception.UnknownSelectorException;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import com.aikitdigital.demoproject.parser.syntaxtree.SearchQueryOperators;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper.typeGroupOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchQueryToQueryDslConverterTest {

	@Mock
	private ConversionService conversionService;

	@InjectMocks
	private SearchQueryToQueryDslConverter converter;

	private final static String STRING_PATH = "name";
	private final static String NUMBER_PATH = "salary";
	private final static String DATE_TIME_PATH = "dateTime";

	@Test
	void shouldFailWhileConvertingComparisonNode_WhenUnknownSelectorIsGiven() {
		var comparisonNode = new ComparisonNode(ComparisonOperator.EQUAL, "non-existent-selector", Optional.of("val"));

		assertThrows(UnknownSelectorException.class, () -> converter.visit(comparisonNode, definitionHelper()));
	}

	@Test
	void shouldFailWhileConvertingComparisonNode_WhenOperatorIsNotValidForGivenSelector() {
		var comparisonNode = new ComparisonNode(ComparisonOperator.GREATER_THAN, STRING_PATH, Optional.of("val"));

		assertThrows(InvalidComparisonOperatorException.class, () -> converter.visit(comparisonNode, definitionHelper()));
	}

	@Test
	void shouldFailWhileConvertingComparisonNode_WhenArgumentValueTypeIsNotValid() {
		var argumentValue = "val";
		var comparisonNode = new ComparisonNode(ComparisonOperator.EQUAL, NUMBER_PATH, Optional.of(argumentValue));
		when(conversionService.convert(argumentValue, Integer.class)).thenThrow(new IllegalArgumentException());

		assertThrows(InvalidArgumentValueFormatException.class, () -> converter.visit(comparisonNode, definitionHelper()));
	}

	@Test
	void shouldFailWhileConvertingComparisonNode_WhenNullArgumentValueIsComparedWithRelationalOperator() {
		var comparisonNode = new ComparisonNode(ComparisonOperator.GREATER_THAN, NUMBER_PATH, Optional.empty());

		assertThrows(InvalidSearchQueryException.class, () -> converter.visit(comparisonNode, definitionHelper()));
	}

	@ParameterizedTest
	@MethodSource({"provideParametersEqualityOperators", "provideParametersRelationalOperators"})
	void shouldCreatePredicateBasedOnOperatorAndSelectorType(ComparisonOperator operator, String selector, String argumentValue, Class<Object> type, Object convertedArgumentValue, String expectedOperator) {
		var comparisonNode = new ComparisonNode(operator, selector, Optional.of(argumentValue));
		when(conversionService.convert(argumentValue, type)).thenReturn(convertedArgumentValue);

		var predicate = converter.visit(comparisonNode, definitionHelper());

		var booleanOperation = ((BooleanOperation) predicate);
		assertThat(booleanOperation.getArg(0)).hasToString(selector);
		assertThat(booleanOperation.getOperator().name()).isEqualTo(expectedOperator);
		assertThat(booleanOperation.getArg(1)).hasToString(argumentValue);
	}

	private static Stream<Arguments> provideParametersEqualityOperators() {
		return Stream.of(
						List.of(ComparisonOperator.EQUAL, "EQ"),
						List.of(ComparisonOperator.NOT_EQUAL, "NE")
				)
				.flatMap(operator ->
						Stream.of(
								Arguments.of(operator.get(0), STRING_PATH, "Thanos", String.class, "Thanos", operator.get(1)),
								Arguments.of(operator.get(0), NUMBER_PATH, "1000", Integer.class, 1000, operator.get(1)),
								Arguments.of(operator.get(0), DATE_TIME_PATH, "2018-01-16T13:03:14.385+01:00", OffsetDateTime.class, OffsetDateTime.parse("2018-01-16T13:03:14.385+01:00"), operator.get(1))
						)
				);
	}

	private static Stream<Arguments> provideParametersRelationalOperators() {
		return Stream.of(
						List.of(ComparisonOperator.GREATER_THAN, "GT"),
						List.of(ComparisonOperator.GREATER_THAN_OR_EQUAL, "GOE"),
						List.of(ComparisonOperator.LESS_THAN, "LT"),
						List.of(ComparisonOperator.LESS_THAN_OR_EQUAL, "LOE")
				)
				.flatMap(operator ->
						Stream.of(
								Arguments.of(operator.get(0), NUMBER_PATH, "1000", Integer.class, 1000, operator.get(1)),
								Arguments.of(operator.get(0), DATE_TIME_PATH, "2018-01-16T13:03:14.385+01:00", OffsetDateTime.class, OffsetDateTime.parse("2018-01-16T13:03:14.385+01:00"), operator.get(1))
						)
				);
	}

	private SearchQueryDefinitionHelper definitionHelper() {
		var expr1 = Expressions.stringPath(STRING_PATH);
		var expr2 = Expressions.numberPath(Integer.class, NUMBER_PATH);
		var expr3 = Expressions.dateTimePath(OffsetDateTime.class, DATE_TIME_PATH);
		return SearchQueryDefinitionHelper.builder()
				.selectors(List.of(
						expr1,
						expr2,
						expr3
				))
				.validOperatorsForTypeGroup(typeGroupOf(expr1), Set.of(ComparisonOperator.EQUAL, ComparisonOperator.NOT_EQUAL))
				.validOperatorsForTypeGroup(typeGroupOf(expr2), SearchQueryOperators.defaultOperators())
				.validOperatorsForTypeGroup(typeGroupOf(expr3), SearchQueryOperators.defaultOperators())
				.entityType(Object.class)
				.build();
	}
}
