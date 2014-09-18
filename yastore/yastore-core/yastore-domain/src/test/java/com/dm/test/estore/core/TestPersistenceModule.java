package com.dm.test.estore.core;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.spring.AutowireHelper;
import com.dm.estore.core.config.InitDataLoader;
import com.dm.estore.core.config.PersistenceModule;
import com.dm.estore.core.springdata.support.EnversRevisionRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(
	repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, 
	basePackages = {
	    "com.dm.estore.core.repository"
	})
@EnableTransactionManagement
public class TestPersistenceModule extends PersistenceModule {
	
	private static final String PERSISTENCE_UNIT_LOCATION = "classpath*:META-INF/test_persistence_domain.xml";

	@Override
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.HSQL)
			.setScriptEncoding("UTF-8")
			.ignoreFailedDrops(true)
			.build();
	}
	
	@Override
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource,
			PersistenceUnitManager persistenceUnitManager,
			PersistenceUnitPostProcessor postProcessor) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.HSQL);
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPersistenceUnitPostProcessors(postProcessor);
		factory.setPersistenceXmlLocation(PERSISTENCE_UNIT_LOCATION);
		factory.setJpaProperties(additionalProperties());

		return factory;
	}
	
	private Properties additionalProperties() {
		final Properties properties = new Properties();
		properties.setProperty("hibernate.archive.autodetection", "class, hbm");
		properties.setProperty("hibernate.hbm2ddl.auto", "create");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.format_sql", "true");
		return properties;
	}

	@Override
	@Bean(initMethod = "loadData")
	public InitDataLoader jpaLoader(AutowireHelper helper) {
		return new TestInitDataLoader();
	}
	
	@Override
	@Bean
	public PersistenceUnitPostProcessor persistenceUnitPostProcessor() {
		return new TestPersistenceUnitPostProcessor();
	}
	
	@Bean
	public Cfg getCfg() {
		final String testHomeFolder = Paths.get("").toAbsolutePath().toString() + File.separator + "build"+ File.separator + "test-base";
    	
		Properties properties = new Properties();
		properties.setProperty(CommonConstants.Cfg.CFG_HOME_FOLDER, testHomeFolder);
		return Cfg.Factory.createConfiguration(properties);
	}
}
