package com.mariastore.core.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contact")
public class UserContact {

	public enum ContactType {
		HOME_PHONE, WORK_PHONE, CELL_PHONE, EMAIL
	}

	private ContactType type;

	private String value;

	private String valueExt;

	public ContactType getType() {
		return type;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueExt() {
		return valueExt;
	}

	public void setValueExt(String valueExt) {
		this.valueExt = valueExt;
	}
}
