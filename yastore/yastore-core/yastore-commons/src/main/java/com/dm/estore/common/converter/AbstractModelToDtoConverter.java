package com.dm.estore.common.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.dm.estore.common.exceptions.ConvertionException;

public abstract class AbstractModelToDtoConverter<SOURCE, TARGET> implements ModelToDtoConverter<SOURCE, TARGET> {

	/**
	 * To be overridden to provide TARGET type factory
	 * @return Instance of TARGET type
	 */
	protected TARGET createTarget() {
		return null;
	}
	
	/**
	 * To be overridden to provide implementation of the converter
	 * @param source The source type instance
	 * @param target The target type instance
	 */
	protected void convertInternal(final SOURCE source, final TARGET target) {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Both source and target are required for converter");
		}
	}
	
	@Override
	public TARGET convert(final SOURCE source) throws ConvertionException
	{
		return convert(source, createTarget());
	}

	@Override
	public TARGET convert(final SOURCE source, final TARGET prototype) throws ConvertionException
	{
		convertInternal(source, prototype);
		return prototype;
	}
		
	@Override
	public List<TARGET> convert(List<SOURCE> source) throws ConvertionException {
		if (!CollectionUtils.isEmpty(source)) {
			List<TARGET> target = new ArrayList<TARGET>(source.size());
			for (SOURCE model : source) {
				target.add(convert(model, createTarget()));
			}
			return target;
		}
		
		return Collections.emptyList();
	}
}
