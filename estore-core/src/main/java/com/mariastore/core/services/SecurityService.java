package com.mariastore.core.services;

import com.mariastore.core.domain.LoginStatus;

public interface SecurityService {
	
	LoginStatus authenticate(String username, String password);
}
