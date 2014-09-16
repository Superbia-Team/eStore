package com.dm.estore.services.user;

import com.dm.estore.common.exceptions.UserAuthenticationException;
import com.dm.estore.common.dto.security.UserContextDto;
import com.dm.estore.core.models.User;

public interface UserService {
	
	public static final String CONTEXT_NAME = "userService";
	
	User currentUser();
	
	UserContextDto currentUserContext();
	
	UserContextDto login(final String userName, final String password) throws UserAuthenticationException;
}
