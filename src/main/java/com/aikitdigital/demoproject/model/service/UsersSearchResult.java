package com.aikitdigital.demoproject.model.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsersSearchResult {

	private List<User> users;
}
