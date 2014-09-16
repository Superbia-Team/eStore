package com.dm.estore.config.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dm.estore.core.services.dto.UserDetailsCustom;
import com.dm.estore.services.user.UserService;

@Component("userDetailsService")
public class StoreUserDetailsService implements UserDetailsService {
	
	@Resource(name = UserService.CONTEXT_NAME)
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!StringUtils.isEmpty(username)) {
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			return new UserDetailsCustom(username, "test", authorities);
		}
		
		throw new UsernameNotFoundException(username);
	}

}
