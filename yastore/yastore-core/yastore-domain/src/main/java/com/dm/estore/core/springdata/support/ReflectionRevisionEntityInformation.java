package com.dm.estore.core.springdata.support;

import org.hibernate.envers.RevisionNumber;
import org.springframework.data.repository.history.support.RevisionEntityInformation;
import org.springframework.data.util.AnnotationDetectionFieldCallback;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * {@link RevisionEntityInformation} that uses reflection to inspect a property annotated with {@link RevisionNumber} to
 * find out about the revision number type.
 * 
 * @author Oliver Gierke
 */
public class ReflectionRevisionEntityInformation implements RevisionEntityInformation {

	private final Class<?> revisionEntityClass;
	private final Class<?> revisionNumberType;

	/**
	 * Creates a new {@link ReflectionRevisionEntityInformation} inspecting the given revision entity class.
	 * 
	 * @param revisionEntityClass must not be {@literal null}.
	 */
	public ReflectionRevisionEntityInformation(Class<?> revisionEntityClass) {

		Assert.notNull(revisionEntityClass);

		AnnotationDetectionFieldCallback fieldCallback = new AnnotationDetectionFieldCallback(RevisionNumber.class);
		ReflectionUtils.doWithFields(revisionEntityClass, fieldCallback);

		this.revisionNumberType = fieldCallback.getType();
		this.revisionEntityClass = revisionEntityClass;

	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionNumberType()
	 */
	public Class<?> getRevisionNumberType() {
		return revisionNumberType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#isDefaultRevisionEntity()
	 */
	public boolean isDefaultRevisionEntity() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionEntityClass()
	 */
	public Class<?> getRevisionEntityClass() {
		return revisionEntityClass;
	}
}