package com.dm.estore.common.exceptions;

public class SearchServerConnectionException extends AbstractRuntimeException {

	private static final long serialVersionUID = 47269704436117977L;

	public SearchServerConnectionException() {
		super();
	}
	
	public SearchServerConnectionException(final String msg, final Throwable e) {
		super(msg, e);
	}
}
