package com.aikitdigital.demoproject.service;

import com.aikitdigital.demoproject.model.OffsetBasedPageRequest;
import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.model.service.User;
import com.aikitdigital.demoproject.model.service.UsersSearchResult;
import reactor.core.publisher.Mono;

public interface UserService {

	Mono<UsersSearchResult> searchUsers(SearchQuery searchQuery, OffsetBasedPageRequest pageable);

	Mono<User> getUserById(Integer userId);
}
