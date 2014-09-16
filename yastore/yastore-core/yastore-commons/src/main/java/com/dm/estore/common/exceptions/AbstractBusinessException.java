package com.dm.estore.common.exceptions;

public class AbstractBusinessException extends RuntimeException {

	private static final long serialVersionUID = -5553234538847789880L;

	public AbstractBusinessException() {
		super();
	}
	
	public AbstractBusinessException(String msg) {
		super(msg);
	}
	
	public AbstractBusinessException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public AbstractBusinessException(Throwable e) {
		super(e);
	}
}
