package com.dm.estore.common.exceptions;

public class ConvertionException extends AbstractBusinessException {

	private static final long serialVersionUID = 1L;

	public ConvertionException() {
		super();
	}
	
	public ConvertionException(String msg) {
		super(msg);
	}
	
	public ConvertionException(Throwable e) {
		super(e);
	}
	
	public ConvertionException(String msg, Throwable e) {
		super(msg, e);
	}
}
