package com.aikitdigital.demoproject.converter;

import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.exception.InvalidSortFieldException;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SortToQueryDslOrderSpecifiersConverterTest {

	private final SortToQueryDslOrderSpecifiersConverter converter = new SortToQueryDslOrderSpecifiersConverter();

	@Test
	void shouldSuccessfullyConvertSortToOrderSpecifierWhenSortFieldExists() {
		var fieldName = "fieldName";
		var sort = Sort.by(fieldName).ascending();

		var orderSpecifiers = converter.convert(sort, SearchQueryDefinitionHelper.builder()
				.selectors(List.of(Expressions.stringPath(fieldName)))
				.build());

		assertThat(orderSpecifiers).hasSize(1);
		var orderSpecifier = orderSpecifiers.get(0);
		assertThat(orderSpecifier.getOrder()).isEqualTo(Order.ASC);
		assertThat(((Path<?>) orderSpecifier.getTarget()).getMetadata().getName()).isEqualTo(fieldName);
	}

	@Test
	void shouldConvertMultipleSortValuesToOrderSpecifierWhenSortFieldExists() {
		var fieldName1 = "fieldName";
		var fieldName2 = "time";
		var sort = Sort.by(fieldName1).ascending()
				.and(Sort.by(fieldName2).descending());

		var orderSpecifiers = converter.convert(sort, SearchQueryDefinitionHelper.builder()
				.selectors(List.of(
						Expressions.stringPath(fieldName1),
						Expressions.stringPath(fieldName2)
				))
				.build());

		assertThat(orderSpecifiers).hasSize(2);

		var orderSpecifier1 = orderSpecifiers.get(0);
		assertThat(orderSpecifier1.getOrder()).isEqualTo(Order.ASC);
		assertThat(((Path<?>) orderSpecifier1.getTarget()).getMetadata().getName()).isEqualTo(fieldName1);

		var orderSpecifier2 = orderSpecifiers.get(1);
		assertThat(orderSpecifier2.getOrder()).isEqualTo(Order.DESC);
		assertThat(((Path<?>) orderSpecifier2.getTarget()).getMetadata().getName()).isEqualTo(fieldName2);
	}

	@Test
	void shouldFailWhileConvertingSortToOrderSpecifierWhenSortFieldDoesNotExist() {
		var sort = Sort.by("non-existent-field").ascending();

		assertThrows(
				InvalidSortFieldException.class,
				() -> converter.convert(sort, SearchQueryDefinitionHelper.builder()
						.selectors(List.of(Expressions.stringPath("fieldName")))
						.build())
		);
	}
}
