package com.dm.estore.search.services.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.dm.estore.common.converter.ModelToDtoConverter;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.Product;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;
import com.dm.estore.search.repository.CatalogSearchRepository;
import com.dm.estore.search.services.CatalogIndexService;

@Service(CatalogIndexService.CONTEXT_NAME)
public class CatalogIndexServiceImpl implements CatalogIndexService {
	
	private ModelToDtoConverter<Product, CatalogItemDto> catalogProductConverter;
	private ModelToDtoConverter<Category, CatalogItemDto> catalogCategoryConverter;
	
	private CatalogSearchRepository catalogSearchRepository;

	@Transactional
	@Override
	public void addToIndex(Product product) {
		CatalogItemDto productDto = catalogProductConverter.convert(product);
		catalogSearchRepository.save(productDto);
		catalogSearchRepository.updateLocalizedProperties(productDto);
	}
	
    @Transactional
    @Override
    public void addToIndex(Category category) {
        CatalogItemDto categoryDto = catalogCategoryConverter.convert(category);
        catalogSearchRepository.save(categoryDto);
        catalogSearchRepository.updateLocalizedProperties(categoryDto);
    }

	@Transactional
	@Override
	public void deleteProductFromIndex(Long id) {
		catalogSearchRepository.delete(CatalogItemDto.generateDocumentId(CatalogItemType.product, id));
	}
	
    @Transactional
    @Override
    public void deleteCategoryFromIndex(Long id) {
        catalogSearchRepository.delete(CatalogItemDto.generateDocumentId(CatalogItemType.category, id));
    }	
	
	@Resource
	public void setProductsRepository(CatalogSearchRepository productsRepository) {
		this.catalogSearchRepository = productsRepository;
	}
	
	@Resource(name = "productModelToCatalogDtoConverter")
	public void setCatalogProductConverter(ModelToDtoConverter<Product, CatalogItemDto> catalogConverter) {
		this.catalogProductConverter = catalogConverter;
	}
	
	@Resource(name = "categoryModelToCatalogDtoConverter")
    public void setCatalogCategoryConverter(ModelToDtoConverter<Category, CatalogItemDto> catalogConverter) {
        this.catalogCategoryConverter = catalogConverter;
    }
}
