package com.dm.estore.core.services.utils;

import com.dm.estore.common.constants.SecurityConstants;
import com.dm.estore.core.models.User;

public class UserUtils {
	
	public static User anonymousUser() {
		return new User(SecurityConstants.USER_NAME_ANONYMOUS);
	}
}
