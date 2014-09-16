package com.dm.estore.core.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.support.MergingPersistenceUnitManager;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.exceptions.ConfigurationException;
import com.dm.estore.common.spring.AutowireHelper;
import com.dm.estore.core.config.impl.InitDataLoaderImpl;
import com.dm.estore.core.springdata.ExtendablePersistenceUnitPostProcessor;
import com.dm.estore.core.springdata.support.EnversRevisionRepositoryFactoryBean;
import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
@EnableJpaRepositories(
	repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, 
	basePackages = {
	    "com.dm.estore.core.repository"
	})
@EnableTransactionManagement
public class PersistenceModule {
	
	private static final String PERSISTENCE_UNIT_LOCATION = "classpath*:META-INF/persistence_*.xml";
	
	@Resource
    private Cfg configuration;
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			PersistenceUnitManager persistenceUnitManager,
			PersistenceUnitPostProcessor postProcessor) {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.HSQL);
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPersistenceUnitPostProcessors(postProcessor);
		factory.setPersistenceXmlLocation(PERSISTENCE_UNIT_LOCATION);
		factory.setJpaProperties(additionalProperties());
		factory.setPersistenceUnitManager(persistenceUnitManager);
		return factory;
	}
	
	@Bean
	public PersistenceUnitPostProcessor persistenceUnitPostProcessor() {
		return new ExtendablePersistenceUnitPostProcessor();
	}
	
	@Bean
	public PersistenceUnitManager persistenceUnitManager(PersistenceUnitPostProcessor postProcessor) {
		MergingPersistenceUnitManager persistenceUnitManager = new MergingPersistenceUnitManager();
		persistenceUnitManager.setPersistenceXmlLocations(PERSISTENCE_UNIT_LOCATION);
		persistenceUnitManager.setDefaultDataSource(dataSource());
		persistenceUnitManager.setPersistenceUnitPostProcessors(postProcessor);
    
    	return persistenceUnitManager;
    
	}
	
	@Bean
	public DataSource dataSource() {
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setDriverClass(readFromConfig(CommonConstants.Cfg.DB.CFG_JDBC_DRIVER, true, null));
		dataSource.setJdbcUrl(resolveJDBCUrl());
		
		final String userName = readFromConfig(CommonConstants.Cfg.DB.CFG_USERNAME, false, null);
		if (userName != null) dataSource.setUsername(userName);
		final String pwrd = readFromConfig(CommonConstants.Cfg.DB.CFG_PSWD, false, null);
		if (pwrd != null) dataSource.setPassword(pwrd);
		
		dataSource.setIdleConnectionTestPeriod(Long.parseLong(readFromConfig(CommonConstants.Cfg.DB.CFG_IDLE_PERIOD, false, "60")), TimeUnit.SECONDS);
		dataSource.setIdleMaxAge(Long.parseLong(readFromConfig(CommonConstants.Cfg.DB.CFG_IDLE_MAX_AGE, false, "240")), TimeUnit.SECONDS);
		dataSource.setMaxConnectionsPerPartition(Integer.parseInt(readFromConfig(CommonConstants.Cfg.DB.CFG_MAX_CON_PER_PART, false, "30")));
		dataSource.setMinConnectionsPerPartition(Integer.parseInt(readFromConfig(CommonConstants.Cfg.DB.CFG_MIN_CON_PER_PART, false, "30")));
		dataSource.setPartitionCount(Integer.parseInt(readFromConfig(CommonConstants.Cfg.DB.CFG_PART_COUNT, false, "3")));
		dataSource.setAcquireIncrement(Integer.parseInt(readFromConfig(CommonConstants.Cfg.DB.CFG_ACQ_INC, false, "5")));
		dataSource.setStatementsCacheSize(Integer.parseInt(readFromConfig(CommonConstants.Cfg.DB.CFG_STMT_CACHE, false, "100")));
		return dataSource;
	}

	private Properties additionalProperties() {
		final Properties properties = new Properties();
		properties.setProperty("hibernate.archive.autodetection", "class, hbm");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.show_sql", readFromConfig(CommonConstants.Cfg.CFG_LOG_TRACE_SQL, false, "false"));
		properties.setProperty("hibernate.format_sql", readFromConfig(CommonConstants.Cfg.CFG_LOG_FORMAT_SQL, false, "false"));
		
		final String dialect = readFromConfig(CommonConstants.Cfg.DB.CFG_DIALECT, false, StringUtils.EMPTY);
		if (!StringUtils.isEmpty(dialect)) {
			properties.setProperty("hibernate.dialect", dialect);
		}
		return properties;
	}
	
	private String resolveJDBCUrl() {
		String jdbcUrl = configuration.getConfig().getString(CommonConstants.Cfg.DB.CFG_JDBC_URL);
		if (StringUtils.isEmpty(jdbcUrl))
			throw new ConfigurationException("Configuration for " + CommonConstants.Cfg.DB.CFG_JDBC_URL + " is not found.");
		if (jdbcUrl.contains(CommonConstants.HOME_PLACEHOLDER)) {
			jdbcUrl = jdbcUrl.replace(CommonConstants.HOME_PLACEHOLDER, configuration.getHomeFolder());
		}
		
		return jdbcUrl;
	}
	
	private String readFromConfig(String propertyName, boolean required, String defaultValue) {
		String value = configuration.getConfig().getString(propertyName);
		if (StringUtils.isEmpty(value)) {
			if (required)
				throw new ConfigurationException("Configuration for " + propertyName + " is not found.");
			else
				return defaultValue;
		}
		
		return value;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	@Bean
    public PersistenceExceptionTranslator persistenceExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
	
	@Bean(initMethod = "loadData")
	public InitDataLoader jpaLoader(AutowireHelper helper) {
		return new InitDataLoaderImpl();
	}
	
	@Bean
	public AutowireHelper autowireHelper() {
	    return AutowireHelper.getInstance();
	}
}
