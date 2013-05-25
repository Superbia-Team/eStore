package com.mariastore.core.services.impl;

import org.springframework.stereotype.Service;

import com.mariastore.core.domain.LoginStatus;
import com.mariastore.core.services.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService {

	@Override
	public LoginStatus authenticate(String username, String password) {
		return new LoginStatus();
	}
}
