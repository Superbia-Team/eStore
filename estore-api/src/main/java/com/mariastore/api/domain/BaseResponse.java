package com.mariastore.api.domain;

import java.io.Serializable;

public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status {
		FAILED, SUCCEED
	}

	private Status status = Status.SUCCEED;

	private String details;
	
	public BaseResponse() {}
	
	public BaseResponse(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
