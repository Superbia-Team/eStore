package com.mariastore.core.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profile")
public class UserProfile extends User {

	private List<Address> addresses = new ArrayList<Address>();
	
	private List<UserContact> contacts = new ArrayList<UserContact>();

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<UserContact> getContacts() {
		return contacts;
	}

	public void setContacts(List<UserContact> contacts) {
		this.contacts = contacts;
	}
}
