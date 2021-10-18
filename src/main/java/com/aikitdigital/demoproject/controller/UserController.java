package com.aikitdigital.demoproject.controller;

import com.aikitdigital.demoproject.mapper.UserMapper;
import com.aikitdigital.demoproject.model.OffsetBasedPageRequest;
import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.model.api.UserDTO;
import com.aikitdigital.demoproject.model.service.UsersSearchResult;
import com.aikitdigital.demoproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@GetMapping
	public Mono<List<UserDTO>> searchUsers(@RequestParam(value = "search", required = false) SearchQuery search,
	                                       @Valid @PositiveOrZero @RequestParam(name = "offset", defaultValue = "0", required = false) Long offset,
	                                       @Valid @Max(25) @Positive @RequestParam(name = "limit", defaultValue = "25", required = false) Integer limit,
	                                       Sort sort) {
		return userService.searchUsers(search, new OffsetBasedPageRequest(offset, limit, sort))
				.map(UsersSearchResult::getUsers)
				.map(userMapper::usersToUserDTOList);
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<UserDTO>> getUserById(@Valid @PositiveOrZero @PathVariable("id") Integer userId) {
		return userService.getUserById(userId)
				.map(userMapper::userToUserDTO)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public void handleConstraintViolationException(ConstraintViolationException e) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid searchUsers request params", e);
	}
}
