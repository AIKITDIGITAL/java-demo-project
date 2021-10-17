package com.aikitdigital.demoproject.mapper;

import com.aikitdigital.demoproject.model.api.UserDTO;
import com.aikitdigital.demoproject.model.data.UserData;
import com.aikitdigital.demoproject.model.service.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

	public abstract User userDataToUser(UserData userData);
	public abstract UserDTO userToUserDTO(User user);

	public abstract List<User> userDataToUserList(List<UserData> userData);
	public abstract List<UserDTO> usersToUserDTOList(List<User> user);
}
