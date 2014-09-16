package com.dm.estore.core.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.dm.estore.core.models.Customer;
import com.dm.estore.core.models.EmailAddress;
import com.dm.estore.core.models.QCustomer;
import com.dm.estore.core.models.User;
import com.dm.estore.core.repository.CustomerRepository;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Plain JPA based implementation of {@link CustomerRepository}.
 * 
 * @author dmorozov
 */
@Repository
class CustomerRepositoryImpl extends QueryDslRepositorySupport implements CustomerRepository {

	public CustomerRepositoryImpl() {
		super(User.class);
	}

	@PersistenceContext
	private EntityManager em;

	/* 
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#findOne(java.lang.Long)
	 */
	@Override
	public User findOne(Long id) {
		return em.find(User.class, id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#save(com.oreilly.springdata.jpa.core.Customer)
	 */
	public User save(User customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#findByEmailAddress(com.oreilly.springdata.jpa.core.EmailAddress)
	 */
	@Override
	public User findByEmailAddress(EmailAddress emailAddress) {

		TypedQuery<User> query = em.createQuery("select c from Customer c where c.emailAddress = :email", User.class);
		query.setParameter("email", emailAddress);

		return query.getSingleResult();
	}

	public Iterable<Customer> findAllLongtermCustomersWithBirthday() {
		QCustomer customer = QCustomer.customer;
		
        return from(customer)
           .where(hasBirthday().and(isLongTermCustomer()))
           .list(customer);
    }
	
	public static BooleanExpression hasBirthday() {
		DateTime today = DateTime.now();
		return QCustomer.customer.birthday.eq(today);
	}

	public static BooleanExpression isLongTermCustomer() {
		DateTime today = DateTime.now();
		return QCustomer.customer.birthday.lt(today.plusMonths(2));
	}
	
	public User switchUserType(final User user, Class<? extends User> targetClass) {
		// Make sure we save any pending changes
		User persistedUser = em.merge(user);
		em.flush();

		// Remove the User instance from the persistence context
		final Session session = (Session) em.getDelegate();
		session.evict(persistedUser);

		// Update the DTYPE
		final String sqlString = "update USERS set USERS.DTYPE = '" + targetClass.getSimpleName() + "' where USERS.id = :id";
		final Query query = em.createNativeQuery(sqlString);
		query.setParameter("id", user.getId());
		query.executeUpdate();

		em.flush();

		// Load the User with its new type
		return em.find(targetClass, persistedUser.getId());
	}
}
