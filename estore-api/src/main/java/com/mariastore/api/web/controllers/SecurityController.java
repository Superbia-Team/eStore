package com.mariastore.api.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mariastore.core.domain.LoginStatus;
import com.mariastore.core.services.SecurityService;

@Controller
public class SecurityController {

	private static Logger log = LoggerFactory
			.getLogger(SecurityController.class);

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<LoginStatus> ajaxLogin(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password", required=false) String password) {
		
		log.debug("Login for: " + username);
		
		return new ResponseEntity<LoginStatus>(securityService.authenticate(
				username, password), HttpStatus.OK);
	}
}
