package com.dm.estore.common.exceptions;

public class AbstractRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -5553234538847789880L;

	public AbstractRuntimeException() {
		super();
	}
	
	public AbstractRuntimeException(String msg) {
		super(msg);
	}
	
	public AbstractRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public AbstractRuntimeException(Throwable e) {
		super(e);
	}
}
