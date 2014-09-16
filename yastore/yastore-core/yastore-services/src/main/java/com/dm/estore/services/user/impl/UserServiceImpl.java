package com.dm.estore.services.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dm.estore.common.exceptions.UserAuthenticationException;
import com.dm.estore.common.dto.security.UserContextDto;
import com.dm.estore.core.models.User;
import com.dm.estore.core.services.dto.UserDetailsCustom;
import com.dm.estore.services.user.UserService;

@Service(value = UserService.CONTEXT_NAME)
public class UserServiceImpl implements UserService {
    
    @Autowired
    AuthenticationManager authenticationManager;

	@Override
	public User currentUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		UserDetailsCustom springSecurityUser = securityContext
				.getAuthentication() != null ? (UserDetailsCustom) securityContext
				.getAuthentication().getPrincipal() : null;
		return springSecurityUser != null ? springSecurityUser.getAccount() : null;
	}

    @Override
    public UserContextDto currentUserContext() {
        UserContextDto context = new UserContextDto();
        
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();
        UserDetailsCustom springSecurityUser = auth != null ? (UserDetailsCustom) auth.getPrincipal() : null;
                
        User user = springSecurityUser != null ? springSecurityUser.getAccount() : null;
        if (user != null) {
            context.setAuthenticated(Boolean.TRUE);
            context.setUserName(user.getUserName());
            context.setFirstName(user.getFirstName());
            context.setLastName(user.getLastName());
            for (GrantedAuthority authority : auth.getAuthorities()) {
                context.getRoles().add(authority.getAuthority());
            }
        }
        
        return context;
    }

    @Override
    public UserContextDto login(String userName, String password) throws UserAuthenticationException {
        try {
            Authentication request = new UsernamePasswordAuthenticationToken( userName, password );
            Authentication result = authenticationManager.authenticate( request );
            SecurityContextHolder.getContext().setAuthentication( result );
            return currentUserContext();
        } catch (AuthenticationException e) {
            throw new UserAuthenticationException(e);
        }
    }
}
