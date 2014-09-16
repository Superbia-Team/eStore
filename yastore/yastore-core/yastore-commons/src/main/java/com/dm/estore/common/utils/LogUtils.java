/*
 * Copyright 2014 Denis Morozov.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dm.estore.common.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.dm.estore.common.constants.CommonConstants;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dmorozov
 */
public class LogUtils {
    
    public static void updateLoggerConfiguration(final String configPath, final String logFolder) throws JoranException {
        
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration

        // inject the configuration properties of the current application
        // property of the LoggerContext
        context.putProperty("application-name", CommonConstants.App.CFG_APP_NAME);
        context.putProperty("log-folder", logFolder);

        jc.doConfigure(configPath);
    }
}
