package com.aikitdigital.demoproject.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

	private Integer userId;
	private String username;
	private String name;
	private String surname;
	private Integer salary;
	private OffsetDateTime from;
	private OffsetDateTime to;
}
