package com.mariastore.api.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "list")
public class RestCollection<T> {

	private List<T> list;

	private int page = 0;

	private int count = 0;

	public RestCollection() {
	}

	public RestCollection(List<T> list) {
		setList(list);
	}

	@XmlElement
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
		count = list != null ? list.size() : 0;
	}

	@XmlElement
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@XmlElement
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
