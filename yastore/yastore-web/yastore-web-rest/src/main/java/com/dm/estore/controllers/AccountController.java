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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.dm.estore.controllers.common.QueryConstants;
import com.dm.estore.core.models.User;
import com.dm.estore.services.search.CatalogSearchService;

/**
 * @author dmorozov
 */
@Controller
@RequestMapping(QueryConstants.API_PROFILE)
public class AccountController {
    
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
    
    @Resource(name = CatalogSearchService.CONTEXT_NAME)
    private CatalogSearchService searchService;
    
    private final Map<DeferredResult<List<String>>, String> searchRequests = new ConcurrentHashMap<DeferredResult<List<String>>, String>();

    // @Secured(Privileges.CAN_USER_READ)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public User currentAccount() {
        String test = "TEST";

        LOG.debug("Get account: " + test);
        return new User(test, test);
    }
    
    @RequestMapping(value="search", method=RequestMethod.GET)
	@ResponseBody
	public DeferredResult<List<String>> getMessages(@RequestParam String searchText) {

		final DeferredResult<List<String>> deferredResult = new DeferredResult<List<String>>(null, Collections.emptyList());
		this.searchRequests.put(deferredResult, searchText);

		deferredResult.onCompletion(new Runnable() {
			@Override
			public void run() {
				searchRequests.remove(deferredResult);
			}
		});

		//List<String> messages = this.searchService.doSearch(searchText);
		List<String> messages = generateDummyMessages(searchText);
		if (!messages.isEmpty()) {
			deferredResult.setResult(messages);
		}

		return deferredResult;
	}
    
    @Deprecated
    private List<String> generateDummyMessages(String searchText) {
    	List<String> searchResult = new ArrayList<String>();
    	final int count = (int) (Math.random() % 10.0);
    	for (int i = 0; i < count; i++) {
    		searchResult.add(searchText + i);
    	}
    	
    	return searchResult;
    }
    
    @RequestMapping("/custom-timeout-handling")
	public @ResponseBody WebAsyncTask<String> callableWithCustomTimeoutHandling() {

		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(2000);
				return "Callable result";
			}
		};

		return new WebAsyncTask<String>(1000, callable);
	}
    
    @RequestMapping("/exception")
	public @ResponseBody Callable<String> callableWithException(
			final @RequestParam(required=false, defaultValue="true") boolean handled) {

		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(2000);
				if (handled) {
					// see handleException method further below
					throw new IllegalStateException("Callable error");
				}
				else {
					throw new IllegalArgumentException("Callable error");
				}
			}
		};
	}
    
    @ExceptionHandler
	@ResponseBody
	public String handleException(IllegalStateException ex) {
		return "Handled exception: " + ex.getMessage();
	}
}
