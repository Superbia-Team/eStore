package com.dm.test.estore.core;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

public class TestPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		// do nothing
	}
}
