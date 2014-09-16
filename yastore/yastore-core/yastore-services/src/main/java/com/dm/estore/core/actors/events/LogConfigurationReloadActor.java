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
package com.dm.estore.core.actors.events;

import akka.actor.UntypedActor;

import com.dm.estore.common.akka.events.ConfigurationChangedEvent;
import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.utils.LogUtils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dmorozov
 */
public class LogConfigurationReloadActor extends UntypedActor {

    public static final String CONTEXT_NAME = "logConfigurationActor";

    private final static Logger LOG = LoggerFactory.getLogger(LogConfigurationReloadActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConfigurationChangedEvent) {
            final String configPath = Cfg.instance().getHomeFolder() + File.separator + Cfg.instance().getConfig().getString(CommonConstants.Cfg.CFG_LOG_CONFIG);
            final String logFolder = Cfg.instance().getHomeFolder() + File.separator + Cfg.instance().getConfig().getString(CommonConstants.Cfg.CFG_LOG_DIR);

            LOG.info("Reload logger configuration: " + configPath);
            LOG.info("    logs: " + logFolder);
            LOG.info("    actor: " + getContext().self().path());

            LogUtils.updateLoggerConfiguration(configPath, logFolder);
        } else {
            unhandled(message);
        }
    }
}
