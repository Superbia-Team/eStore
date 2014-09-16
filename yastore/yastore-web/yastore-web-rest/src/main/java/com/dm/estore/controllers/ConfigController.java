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
package com.dm.estore.controllers;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.controllers.common.QueryConstants;
import com.dm.estore.common.dto.ServerConfigDto;

/**
 * @author dmorozov
 */
@Controller
@RequestMapping(QueryConstants.API_CONFIG)
public class ConfigController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigController.class);
    
    @Resource
    private Cfg configuration;
    
    @RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public ServerConfigDto getServerConfig() {
        
        LOG.debug("Query server's config Thread: [{}]", Thread.currentThread().getName());
        ServerConfigDto serverConfig = new ServerConfigDto();
        serverConfig.setServerPort(configuration.getConfig().getInt(CommonConstants.Server.PROP_PORT, CommonConstants.Server.Defaults.DEFAULT_HTTP_PORT));
        serverConfig.setServerPortSsl(configuration.getConfig().getInt(CommonConstants.Server.PROP_PORT_SSL, CommonConstants.Server.Defaults.DEFAULT_HTTP_PORT_SSL));
        
        return serverConfig;
	}
}
