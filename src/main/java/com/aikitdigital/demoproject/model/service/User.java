package com.aikitdigital.demoproject.model.service;

import com.querydsl.core.annotations.QueryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@QueryEntity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Integer userId;
	private String username;
	private String name;
	private String surname;
	private Integer salary;
	private OffsetDateTime from;
	private OffsetDateTime to;
}
