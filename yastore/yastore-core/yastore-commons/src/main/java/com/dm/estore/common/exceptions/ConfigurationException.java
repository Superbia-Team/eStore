package com.dm.estore.common.exceptions;

public class ConfigurationException extends AbstractRuntimeException {

	private static final long serialVersionUID = 6238820868914714857L;

	public ConfigurationException() {
		super();
	}
	
	public ConfigurationException(String msg) {
		super(msg);
	}
	
	public ConfigurationException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public ConfigurationException(Throwable e) {
		super(e);
	}
}
