package com.mariastore.api.web.controllers;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mariastore.core.domain.LoginStatus;
import com.mariastore.core.domain.UserProfile;
import com.mariastore.core.services.SecurityService;
import com.mariastore.core.services.UserProfileService;

@Controller
public class SecurityController {

	private static Logger log = LoggerFactory.getLogger(SecurityController.class);

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private UserProfileService profileService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<LoginStatus> ajaxLogin(@RequestParam(value = "username") String username,
			@RequestParam(value = "password", required = false) String password) {

		log.debug("Login for: " + username);

		return new ResponseEntity<LoginStatus>(securityService.authenticate(username, password), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/userProfile", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserProfile> userProfile() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && !StringUtils.isEmpty(auth.getName()) 
				&& !auth.getName().equals("anonymousUser") && auth.isAuthenticated()) {
			
			log.debug("Login for: " + auth.getName());

			return new ResponseEntity<UserProfile>(profileService.findByUserName(auth.getName()), HttpStatus.OK);
		}

		return new ResponseEntity<UserProfile>(HttpStatus.NOT_FOUND);
	}
	
}
