package com.aikitdigital.demoproject.configuration;

import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.model.service.QUser;
import com.aikitdigital.demoproject.model.service.User;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import com.aikitdigital.demoproject.parser.syntaxtree.SearchQueryOperators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;

import java.util.List;
import java.util.Set;

import static com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper.typeGroupOf;

@Configuration
public class SearchQueryConfiguration {

	private static final String SORT_PARAMETER = "order";
	private static final String SORT_PROPERTY_DELIMITER = ":";

	@Bean
	public SearchQueryDefinitionHelper usersSearchQueryDefinition() {
		return SearchQueryDefinitionHelper.builder()
				.entityType(User.class)
				.selectors(List.of(
						QUser.user.name,
						QUser.user.username,
						QUser.user.surname,
						QUser.user.from,
						QUser.user.to,
						QUser.user.userId,
						QUser.user.salary
				))
				.validOperatorsForTypeGroup(typeGroupOf(QUser.user.name), Set.of(ComparisonOperator.EQUAL, ComparisonOperator.NOT_EQUAL))
				.validOperatorsForTypeGroup(typeGroupOf(QUser.user.from), SearchQueryOperators.defaultOperators())
				.validOperatorsForTypeGroup(typeGroupOf(QUser.user.userId), SearchQueryOperators.defaultOperators())
				.build();
	}

	@Bean
	public ReactiveSortHandlerMethodArgumentResolver reactiveSortHandlerMethodArgumentResolver() {
		var sortResolver = new ReactiveSortHandlerMethodArgumentResolver();
		sortResolver.setSortParameter(SORT_PARAMETER);
		sortResolver.setPropertyDelimiter(SORT_PROPERTY_DELIMITER);
		return sortResolver;
	}

}
