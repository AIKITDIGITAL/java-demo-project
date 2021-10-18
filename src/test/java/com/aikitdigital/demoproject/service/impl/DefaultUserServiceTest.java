package com.aikitdigital.demoproject.service.impl;

import com.aikitdigital.demoproject.converter.SearchQueryToQueryDslConverter;
import com.aikitdigital.demoproject.converter.SortToQueryDslOrderSpecifiersConverter;
import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.mapper.UserMapper;
import com.aikitdigital.demoproject.model.OffsetBasedPageRequest;
import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.model.data.UserData;
import com.aikitdigital.demoproject.model.data.UserDataResponse;
import com.aikitdigital.demoproject.model.service.QUser;
import com.aikitdigital.demoproject.model.service.User;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonNode;
import com.aikitdigital.demoproject.parser.syntaxtree.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultUserServiceTest {

	@Mock
	private WebClient webClient;
	@Spy
	private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
	@Mock
	private SearchQueryToQueryDslConverter searchQueryToQueryDslConverter;
	@Mock
	private SortToQueryDslOrderSpecifiersConverter sortToQueryDslOrderSpecifiersConverter;
	@Mock
	private SearchQueryDefinitionHelper searchQueryDefinitionHelper;

	@InjectMocks
	private DefaultUserService defaultUserService;

	@Test
	void shouldFetchUserWhenUserExists() {
		var userId = 100;
		givenUsersExist(userId);

		var user = defaultUserService.getUserById(userId).block();

		assertThat(user).isNotNull();
		assertThat(user.getUserId()).isEqualTo(userId);
	}

	@Test
	void shouldReturnNoUserWhenRequestedUserDoesNotExist() {
		givenUsersExist(1);

		var user = defaultUserService.getUserById(2).blockOptional();

		assertThat(user).isEmpty();
	}

	@Test
	void shouldReturnUsersWhenNoSearchQueryIsSpecified() {
		givenUsersExist(3, 1, 2);

		var searchResult = defaultUserService.searchUsers(null, createPageable()).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(3)
				.containsExactly(
						User.builder().userId(3).build(),
						User.builder().userId(1).build(),
						User.builder().userId(2).build()
				);
	}

	@Test
	void shouldLimitUsersWhenPageableValuesAreSpecified() {
		givenUsersExist(3, 1, 2);

		var searchResult = defaultUserService.searchUsers(null, createPageable(1, 1, Sort.unsorted())).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(1)
				.containsExactly(
						User.builder().userId(1).build()
				);
	}

	@Test
	void shouldSortUsersByNumberTypeAscWhenSortAscIsDefined() {
		givenUsersExist(1, 2, 3);
		var sort = Sort.by("userId").ascending();
		when(sortToQueryDslOrderSpecifiersConverter.convert(eq(sort), any())).thenReturn(List.of(
				QUser.user.userId.asc()
		));

		var searchResult = defaultUserService.searchUsers(null, createPageable(0, 3, sort)).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(3)
				.containsExactly(
						User.builder().userId(1).build(),
						User.builder().userId(2).build(),
						User.builder().userId(3).build()
				);
	}

	@Test
	void shouldSortUsersByNumberTypeDescWhenSortDescIsDefined() {
		givenUsersExist(1, 2, 3);
		var sort = Sort.by("userId").descending();
		when(sortToQueryDslOrderSpecifiersConverter.convert(eq(sort), any())).thenReturn(List.of(
				QUser.user.userId.desc()
		));

		var searchResult = defaultUserService.searchUsers(null, createPageable(0, 3, sort)).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(3)
				.containsExactly(
						User.builder().userId(3).build(),
						User.builder().userId(2).build(),
						User.builder().userId(1).build()
				);
	}

	@Test
	void shouldSortUsersByStringTypeInAlphabeticalOrderWhenSortForUserNameIsDefined() {
		givenUsersExist("z", "A", "h");
		var sort = Sort.by("name").ascending();
		when(sortToQueryDslOrderSpecifiersConverter.convert(eq(sort), any())).thenReturn(List.of(
				QUser.user.name.asc()
		));

		var searchResult = defaultUserService.searchUsers(null, createPageable(0, 3, sort)).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(3)
				.containsExactly(
						User.builder().name("A").build(),
						User.builder().name("h").build(),
						User.builder().name("z").build()
				);
	}

	@Test
	void shouldSortUsersByDateTimeTypeAscWhenSortForDateFromIsDefined() {
		var first = OffsetDateTime.parse("2018-01-16T13:03:14.385+01:00");
		var second = OffsetDateTime.parse("2018-01-16T13:03:17+01:00");
		var third = OffsetDateTime.parse("2018-01-20T10:00+01:00");
		givenUsersExist(second, third, first);
		var sort = Sort.by("from").ascending();
		when(sortToQueryDslOrderSpecifiersConverter.convert(eq(sort), any())).thenReturn(List.of(
				QUser.user.from.asc()
		));

		var searchResult = defaultUserService.searchUsers(null, createPageable(0, 3, sort)).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(3)
				.containsExactly(
						User.builder().from(first).build(),
						User.builder().from(second).build(),
						User.builder().from(third).build()
				);
	}

	@Test
	void shouldLimitFirstAndThenSortUsersWhenBothLimitAndSortValuesAreSpecified() {
		givenUsersExist(3, 1, 2);
		var sort = Sort.by("userId").ascending();
		when(sortToQueryDslOrderSpecifiersConverter.convert(eq(sort), any())).thenReturn(List.of(
				QUser.user.userId.asc()
		));

		var searchResult = defaultUserService.searchUsers(null, createPageable(0, 2, sort)).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(2)
				.containsExactly(
						User.builder().userId(1).build(),
						User.builder().userId(3).build()
				);
	}

	@Test
	void shouldFilterUsersByDateTimeTypeWhenUserFromFilterIsSpecified() {
		var first = OffsetDateTime.parse("2018-01-16T13:03:14.385+01:00");
		var second = OffsetDateTime.parse("2018-01-16T13:03:17+01:00");
		var third = OffsetDateTime.parse("2018-01-20T10:00+01:00");
		givenUsersExist(second, third, first);
		var comparisonNode = someComparisonNode();
		when(searchQueryToQueryDslConverter.visit(eq(comparisonNode), any())).thenReturn(
				QUser.user.from.eq(third)
		);

		var searchResult = defaultUserService.searchUsers(new SearchQuery(comparisonNode), createPageable()).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(1)
				.containsExactly(
						User.builder().from(third).build()
				);
	}

	@Test
	void shouldFilterUsersByStringTypeWhenUserNameFilterIsSpecified() {
		givenUsersExist("z", "A", "h");
		var comparisonNode = someComparisonNode();
		when(searchQueryToQueryDslConverter.visit(eq(comparisonNode), any())).thenReturn(
				QUser.user.name.eq("h")
		);

		var searchResult = defaultUserService.searchUsers(new SearchQuery(comparisonNode), createPageable()).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(1)
				.containsExactly(
						User.builder().name("h").build()
				);
	}

	@Test
	void shouldFilterUsersByNumberTypeWhenUserIdFilterIsSpecified() {
		givenUsersExist(3, 1, 2);
		var comparisonNode = someComparisonNode();
		when(searchQueryToQueryDslConverter.visit(eq(comparisonNode), any())).thenReturn(
				QUser.user.userId.eq(1)
		);

		var searchResult = defaultUserService.searchUsers(new SearchQuery(comparisonNode), createPageable()).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers())
				.hasSize(1)
				.containsExactly(
						User.builder().userId(1).build()
				);
	}

	@Test
	void shouldReturnEmptyListOfUsersWhenDownstreamServiceFails() {
		whenWebClientRequestThenReturn(Mono.error(mock(WebClientResponseException.class)));

		var searchResult = defaultUserService.searchUsers(null, createPageable()).block();

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getUsers()).isEmpty();
	}

	private ComparisonNode someComparisonNode() {
		return new ComparisonNode(ComparisonOperator.EQUAL, "tmp", Optional.of("1"));
	}

	private OffsetBasedPageRequest createPageable(long offset, int limit, Sort sort) {
		return new OffsetBasedPageRequest(offset, limit, sort);
	}

	private OffsetBasedPageRequest createPageable() {
		return new OffsetBasedPageRequest(0, 10, Sort.unsorted());
	}

	private void givenUsersExist(int... userId) {
		whenWebClientRequestThenReturn(Mono.just(createUserDataResponse(userId)));
	}

	private void givenUsersExist(String... name) {
		whenWebClientRequestThenReturn(Mono.just(createUserDataResponse(name)));
	}

	private void givenUsersExist(OffsetDateTime... fromDateTime) {
		whenWebClientRequestThenReturn(Mono.just(createUserDataResponse(fromDateTime)));
	}

	private UserDataResponse createUserDataResponse(OffsetDateTime... fromDateTime) {
		return new UserDataResponse(Arrays.stream(fromDateTime)
				.map(dateTime -> UserData.builder()
						.from(dateTime)
						.build())
				.collect(Collectors.toList())
		);
	}

	private UserDataResponse createUserDataResponse(String... name) {
		return new UserDataResponse(Arrays.stream(name)
				.map(nameVal -> UserData.builder()
						.name(nameVal)
						.build())
				.collect(Collectors.toList())
		);
	}

	private UserDataResponse createUserDataResponse(int... userId) {
		return new UserDataResponse(Arrays.stream(userId)
				.mapToObj(id -> UserData.builder()
						.userId(id)
						.build())
				.collect(Collectors.toList())
		);
	}

	private void whenWebClientRequestThenReturn(Mono<UserDataResponse> userDataResponse) {
		var uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		when(webClient.get()).thenReturn(uriSpec);
		var headersSpec = mock(WebClient.RequestHeadersSpec.class);
		when(uriSpec.uri("/users")).thenReturn(headersSpec);
		var responseSpec = mock(WebClient.ResponseSpec.class);
		when(headersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(UserDataResponse.class)).thenReturn(userDataResponse);
	}
}
