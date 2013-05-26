package com.mariastore.api.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

@Service("restAuthenticationEntryPoint")
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private static final String MSG_BAD_CREDENTIALS = "error.login.bad.credentials";
	private static final String MSG_ACCESS_DENIED = "error.login.access.denied";
	private static final String MSG_UNAUTHORIZED = "error.login.unauthorized";
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, 
			AuthenticationException authException) throws IOException {
		
		String errorMsgId = "";
		if (authException instanceof BadCredentialsException) {
			errorMsgId = MSG_BAD_CREDENTIALS;
		} else if (authException instanceof InsufficientAuthenticationException) {
			errorMsgId = MSG_UNAUTHORIZED;
		}

		final String errorMsg = messageSource.getMessage(errorMsgId, null, request.getLocale());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
	}
}