package com.dm.estore.core.services.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.dm.estore.common.constants.SecurityConstants;
import com.dm.estore.core.models.User;
import com.dm.estore.core.repository.UserRepository;
import com.dm.estore.services.user.UserService;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {
	
	@Resource(name = UserService.CONTEXT_NAME)
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User getCurrentAuditor() {
		User user = userService.currentUser();
		if (user == null) {
			user = userRepository.findByUserName(SecurityConstants.USER_NAME_SYSTEM);
		}
		return user;
	}
}
