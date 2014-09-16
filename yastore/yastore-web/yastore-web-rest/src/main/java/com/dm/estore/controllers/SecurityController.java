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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dm.estore.common.exceptions.UserAuthenticationException;
import com.dm.estore.controllers.common.QueryConstants;
import com.dm.estore.common.dto.security.UserContextDto;
import com.dm.estore.services.user.UserService;

/**
 * @author dmorozov
 */
@Controller
@RequestMapping(QueryConstants.API_SECURITY)
public class SecurityController {
    
    private static final Logger LOG = LoggerFactory.getLogger(SecurityController.class);
    
    @Resource(name = UserService.CONTEXT_NAME)
    private UserService userService;
    
    @RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	public UserContextDto authenticate(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password) {
        
        LOG.debug("Authentication request for user '{}'. Thread: [{}]", userName, Thread.currentThread().getName());
        
        try {
            userService.login(userName, password);
            return userService.currentUserContext();
        } catch (UserAuthenticationException e) {
            LOG.warn("Unable to authenticate userl '{}'", userName);
        }
        
        return new UserContextDto();
	}
    
    @RequestMapping(value="logout", method=RequestMethod.POST)
    @ResponseBody
    public UserContextDto logout(HttpServletRequest request, HttpServletResponse response) {
        
        LOG.debug("Logout request. Thread: [{}]", Thread.currentThread().getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){    
           new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
      
        return new UserContextDto();
    }
    
    @RequestMapping(value="context", method=RequestMethod.GET)
    @ResponseBody
    public UserContextDto securityContext() {
        LOG.debug("Logout request. Thread: [{}]", Thread.currentThread().getName());        
        return userService.currentUserContext();
    }
}
