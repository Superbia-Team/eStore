package com.mariastore.core.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginStatus {

	public enum Status {
		SUCCESS, FAILURE
	};

	private LoginStatus.Status status = Status.SUCCESS;

	public LoginStatus() {
	}

	public LoginStatus(LoginStatus.Status status) {
		this.status = status;
	}

	public LoginStatus.Status getStatus() {
		return status;
	}

	public void setStatus(LoginStatus.Status status) {
		this.status = status;
	}
}
