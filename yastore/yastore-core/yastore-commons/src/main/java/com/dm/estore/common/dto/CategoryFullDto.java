package com.dm.estore.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryFullDto extends CategoryDto {

    private Map<String, String> names;
    
    private Map<String, String> summaries;
    
    private List<String> tags;
    
    private List<CategoryFullDto> subcategories = new ArrayList<CategoryFullDto>();

    public Map<String, String> getNames() {
        return names;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public Map<String, String> getSummaries() {
        return summaries;
    }

    public void setSummaries(Map<String, String> summaries) {
        this.summaries = summaries;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<CategoryFullDto> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryFullDto> subcategories) {
        this.subcategories = subcategories;
    }
}
