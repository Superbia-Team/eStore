package com.dm.estore.common.exceptions;

public class UserAuthenticationException extends AbstractBusinessException {

	private static final long serialVersionUID = -5553234538847789880L;

	public UserAuthenticationException() {
		super();
	}
	
	public UserAuthenticationException(String msg) {
		super(msg);
	}
	
	public UserAuthenticationException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public UserAuthenticationException(Throwable e) {
		super(e);
	}
}
