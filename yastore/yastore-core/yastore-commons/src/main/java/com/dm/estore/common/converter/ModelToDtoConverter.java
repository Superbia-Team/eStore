package com.dm.estore.common.converter;

import java.util.List;

import com.dm.estore.common.exceptions.ConvertionException;

/**
 * Interface for a converter that transforms an object of one type into an object of another type
 * 
 * @param <SOURCE> the type of the source object
 * @param <TARGET> the type of the destination object
 */
public interface ModelToDtoConverter<SOURCE, TARGET> {

	/**
	 * Converts the source object, creating a new instance of the destination type
	 * 
	 * @param source The source object to be converted
	 * @return The converted object
	 * @throws ConvertionException Exception if an error occurs
	 */
	TARGET convert(SOURCE source) throws ConvertionException;
	
	/**
	 * Converts the source object into provided destination type
	 * 
	 * @param source The source object to be converted
	 * @param prototype The target object to convert into
	 * @return The converted object
	 * @throws ConvertionException Exception if an error occurs
	 */
	TARGET convert(SOURCE source, TARGET prototype) throws ConvertionException;
	
	/**
	 * Convert list of source object into destination type
	 * @param source List of source objects
	 * @return The converted list of destination type
	 * @throws ConvertionException Exception if an error occurs
	 */
	List<TARGET> convert(List<SOURCE> sources) throws ConvertionException;
}
