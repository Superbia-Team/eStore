package com.dm.estore.common.exceptions;

public class ImmutableStateException extends AbstractBusinessException {

	private static final long serialVersionUID = -5553234538847789880L;

	public ImmutableStateException() {
		super();
	}
	
	public ImmutableStateException(String msg) {
		super(msg);
	}
	
	public ImmutableStateException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public ImmutableStateException(Throwable e) {
		super(e);
	}
}
