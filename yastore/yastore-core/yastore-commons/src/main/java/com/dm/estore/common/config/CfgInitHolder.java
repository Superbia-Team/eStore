package com.dm.estore.common.config;

import java.util.Properties;

/**
 * Helper "holder" class to pass data from bootstrap to the web application run on embedded Jetty.
 * Jetty will use separate class loader and all static members for all classes will be initialized again.
 * 
 * @author dmorozov
 */
public class CfgInitHolder {
	public static Properties commandLineProperties;
}
