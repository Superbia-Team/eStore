package com.dm.estore.search.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = DocumentDto.CORE_NAME)
public class DocumentDto implements Serializable {

	private static final long serialVersionUID = 6259009549868399927L;
	
	public static final String CORE_NAME = "documents";
	public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";

	@Id
	@Field
	private String id;
	
	@Field
	private String name;
	
	@Field("description")
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
