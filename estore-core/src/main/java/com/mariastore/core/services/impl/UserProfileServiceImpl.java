package com.mariastore.core.services.impl;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.mariastore.core.domain.Address;
import com.mariastore.core.domain.UserProfile;
import com.mariastore.core.domain.Address.AddressType;
import com.mariastore.core.services.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

	@Override
	public UserProfile findByUserName(String username) {
		UserProfile userProfile = new UserProfile();
		
		userProfile.setEmail(username);
		userProfile.setFirstName("John");
		userProfile.setLastName("Doe");
		userProfile.setId(-1);
		
		Address address = new Address();
		address.setType(AddressType.SHIPPING);
		address.setAddress1("12 Somestreet");
		address.setCity("Someville");
		address.setCountry(Locale.US.getCountry());
		userProfile.getAddresses().add(address);
		
		return userProfile;
	}

}
