package com.dm.estore.core.services.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDetailsCustom extends User {
	
	private static final long serialVersionUID = -4951904405327142391L;

	public UserDetailsCustom(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public UserDetailsCustom(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

	public com.dm.estore.core.models.User getAccount() {
		return new com.dm.estore.core.models.User(getUsername(), "");
	}
}
