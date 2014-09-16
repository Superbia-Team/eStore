package com.dm.estore.core.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

@Entity(name = "I18N_REF")
public class I18NString implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
    	name = "I18N", 
    	joinColumns = @JoinColumn(name="JOIN_REF_ID")    	
    )
    @MapKeyColumn(name = "LOCALE")
    private Map<Locale, I18NStringValue> strings = new HashMap<Locale, I18NStringValue>();

    public I18NString() {}
    public I18NString(Map<Locale, I18NStringValue> map) {
        this.strings = map;
    }

    public void addString(Locale locale, String text) {
        strings.put(locale, new I18NStringValue(text));
    }

    public String getString(Locale locale) {
    	I18NStringValue returnValue = strings.get(locale);
    	if (returnValue == null && strings.size() == 1) {
    		returnValue = strings.values().iterator().next();
    	}
        return returnValue != null ? returnValue.getValue() : null;
    }
    
    public Map<Locale, I18NStringValue> getAll() {
    	return strings;
    }
}
