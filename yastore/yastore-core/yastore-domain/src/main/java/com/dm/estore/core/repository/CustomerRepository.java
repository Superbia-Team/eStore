package com.dm.estore.core.repository;

import org.springframework.data.repository.Repository;

import com.dm.estore.core.models.EmailAddress;
import com.dm.estore.core.models.User;

/**
 * {@link Repository} to access {@link User} instances.
 * 
 * @author dmorozov
 */
public interface CustomerRepository extends Repository<User, Long> {

	/**
	 * Returns the {@link User} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	User findOne(Long id);

	/**
	 * Saves the given {@link User}.
	 * 
	 * @param customer the {@link User} to search for.
	 * @return
	 */
	User save(User customer);

	/**
	 * Returns the customer with the given {@link EmailAddress}.
	 * 
	 * @param emailAddress the {@link EmailAddress} to search for.
	 * @return
	 */
	User findByEmailAddress(EmailAddress emailAddress);
	
	User switchUserType(final User user, Class<? extends User> targetClass);
}
