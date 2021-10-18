package com.aikitdigital.demoproject.model.api;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDTO {

	private Integer userId;
	private String username;
	private String name;
	private String surname;
	private Integer salary;
	private OffsetDateTime from;
	private OffsetDateTime to;
}
