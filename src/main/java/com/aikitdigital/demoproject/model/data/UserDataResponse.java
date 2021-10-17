package com.aikitdigital.demoproject.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataResponse {

	private List<UserData> users = new ArrayList<>();
}
