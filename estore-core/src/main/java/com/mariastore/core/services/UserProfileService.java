package com.mariastore.core.services;

import com.mariastore.core.domain.UserProfile;

public interface UserProfileService {
	UserProfile findByUserName(String username);
}
