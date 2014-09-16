package com.dm.estore.search.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = CatalogItemDto.CORE_NAME)
public class CatalogItemDto implements Serializable {

	private static final long serialVersionUID = 6259009549868399927L;
	
	public static final String CORE_NAME = "catalog";	
	
	public static final String FIELD_DOCUMENT_ID = "documentId";
	public static final String FIELD_DOCUMENT_TYPE = "documentType";
	public static final String FIELD_ID = "id";
	
	public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NAMES = "name_";
    public static final String FIELD_SUMMARY = "summary";
    public static final String FIELD_SUMMARIES = "summary_";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DESCRIPTIONS = "description_";
    public static final String FIELD_INSTOCK = "inStock";
    public static final String FIELD_POPULARITY = "popularity";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_ACTIVE = "active";
    
    public static final String FIELD_LISTIMAGE = "listImage";
    public static final String FIELD_IMAGE = "image";
    public static final String FIELD_IMAGES = "images";
    
    public static final String FIELD_VARIANT_GROUP = "variantGroup";
    public static final String FIELD_VARIANT_SUBGROUP = "variantSubGroup";
    
    @Id
	@Field(FIELD_ID)
	private String id;

    @Field(FIELD_DOCUMENT_ID)
    private Long documentId;
    
	@Field(FIELD_DOCUMENT_TYPE)
	private CatalogItemType documentType;
	
	@Field(FIELD_CODE)
    private String code;
	
	@Field(FIELD_NAME)
	private String name;
	
	@Field(FIELD_SUMMARY)
	private String summary;
	
	@Field(FIELD_DESCRIPTION)
	private String description;

	@Field(FIELD_INSTOCK)
	private boolean inStock;
	
	@Field(FIELD_POPULARITY)
	private int popularity;
	
	@Field(FIELD_PRICE)
	private float price;
	
	@Field(FIELD_CATEGORY)
	private List<String> categories;
	
	@Field(FIELD_TAG)
	private List<String> tags;
	
	private Map<String, String> names = new HashMap<String, String>();
	
	private Map<String, String>  summaries = new HashMap<String, String>();
	
	private Map<String, String>  descriptions = new HashMap<String, String>();
	
	@Field(FIELD_ACTIVE)
	private boolean active;
	
	@Field(FIELD_IMAGES)
    private List<String> images;
	
	@Field(FIELD_IMAGE)
    private String image;
	
	@Field(FIELD_LISTIMAGE)
    private String listImage;
	
	@Field(FIELD_VARIANT_GROUP)
	private String variantGroup;
	    
	@Field(FIELD_VARIANT_SUBGROUP)
	private String variantSubGroup;
	
	public CatalogItemDto() {}
	public CatalogItemDto(CatalogItemType type) {
	    documentType = type;
	}

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

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

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

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
        setId(CatalogItemDto.generateDocumentId(getDocumentType(), documentId));
    }
    
    public CatalogItemType getDocumentType() {
        return documentType;
    }
    public void setDocumentType(CatalogItemType documentType) {
        this.documentType = documentType;
        setId(CatalogItemDto.generateDocumentId(documentType, getDocumentId()));
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getListImage() {
        return listImage;
    }
    public void setListImage(String listImage) {
        this.listImage = listImage;
    }
    public String getVariantGroup() {
        return variantGroup;
    }
    public void setVariantGroup(String variantGroup) {
        this.variantGroup = variantGroup;
    }
    public String getVariantSubGroup() {
        return variantSubGroup;
    }
    public void setVariantSubGroup(String variantSubGroup) {
        this.variantSubGroup = variantSubGroup;
    }
    public static String generateDocumentId(CatalogItemType type, Long id) {
        return type.name() + "_" + id;
    }
}
