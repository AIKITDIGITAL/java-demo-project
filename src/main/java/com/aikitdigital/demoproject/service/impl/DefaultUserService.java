package com.aikitdigital.demoproject.service.impl;

import com.aikitdigital.demoproject.converter.SearchQueryToQueryDslConverter;
import com.aikitdigital.demoproject.converter.SortToQueryDslOrderSpecifiersConverter;
import com.aikitdigital.demoproject.converter.helper.SearchQueryDefinitionHelper;
import com.aikitdigital.demoproject.mapper.UserMapper;
import com.aikitdigital.demoproject.model.OffsetBasedPageRequest;
import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.model.data.UserData;
import com.aikitdigital.demoproject.model.data.UserDataResponse;
import com.aikitdigital.demoproject.model.service.User;
import com.aikitdigital.demoproject.model.service.UsersSearchResult;
import com.aikitdigital.demoproject.service.UserService;
import com.querydsl.collections.CollQueryFactory;
import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

	private static final String USERS_URL_PATH = "/users";

	private final WebClient webClient;
	private final UserMapper userMapper;

	private final SearchQueryToQueryDslConverter searchQueryToQueryDslConverter;
	private final SortToQueryDslOrderSpecifiersConverter sortToQueryDslOrderSpecifiersConverter;
	private final SearchQueryDefinitionHelper usersSearchQueryDefinition;

	@Override
	public Mono<UsersSearchResult> searchUsers(SearchQuery searchQuery, OffsetBasedPageRequest pageable) {
		final var searchQueryPredicate = createSearchQueryPredicate(searchQuery);
		final var orderSpecifications = getOrderSpecifiers(pageable.getSort());
		return fetchUsers()
				.map(users -> filterUsers(users, pageable, searchQueryPredicate))
				.map(filteredUsers -> sortUsersIfNeeded(filteredUsers, pageable, orderSpecifications))
				.map(UsersSearchResult::new);
	}

	@Override
	public Mono<User> getUserById(Integer userId) {
		return fetchUsers()
				.mapNotNull(users -> filterUsersById(users, userId)
						.orElse(null)
				);
	}

	private Predicate createSearchQueryPredicate(SearchQuery searchQuery) {
		return Optional.ofNullable(searchQuery)
				.map(query -> searchQuery.getNode().accept(searchQueryToQueryDslConverter, usersSearchQueryDefinition))
				.orElseGet(() -> Expressions.asBoolean(true).isTrue());
	}

	private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
		var orderSpecifiers = sortToQueryDslOrderSpecifiersConverter.convert(sort, usersSearchQueryDefinition);
		return orderSpecifiers.toArray(OrderSpecifier[]::new);
	}

	private Mono<List<User>> fetchUsers() {
		return webClient.get()
				.uri(USERS_URL_PATH)
				.retrieve()
				.bodyToMono(UserDataResponse.class)
				.map(UserDataResponse::getUsers)
				.doOnNext(this::logOnSuccess)
				.doOnError(this::logOnError)
				.onErrorReturn(List.of())
				.map(userMapper::userDataToUserList);
	}

	private List<User> filterUsers(List<User> users, OffsetBasedPageRequest pageable, Predicate predicate) {
		return CollQueryFactory
				.from(Alias.alias(User.class), users)
				.where(predicate)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
	}

	private Optional<User> filterUsersById(List<User> users, Integer userId) {
		return users.stream()
				.filter(user -> userId.equals(user.getUserId()))
				.findFirst();
	}

	private List<User> sortUsersIfNeeded(List<User> filteredUsers, OffsetBasedPageRequest pageable, OrderSpecifier<?>[] orderSpecifications) {
		return pageable.getSort().isUnsorted() ? filteredUsers : sortUsers(filteredUsers, orderSpecifications);
	}

	private List<User> sortUsers(List<User> users, OrderSpecifier<?>[] orderSpecifiers) {
		return CollQueryFactory
				.from(Alias.alias(User.class), users)
				.orderBy(orderSpecifiers)
				.fetch();
	}

	private void logOnSuccess(List<UserData> userData) {
		log.debug("User data were successfully fetched. userData={}", userData);
	}

	private void logOnError(Throwable throwable) {
		log.error("Error occurred while fetching user data", throwable);
	}
}
