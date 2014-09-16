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

package com.dm.estore.common.akka.events;

import com.dm.estore.common.constants.CommonConstants;

/**
 *
 * @author dmorozov
 */
public class ModelChangedEvent extends BusEvent<Void> {
	
	public enum ChangeType {CREATE, UPDATE, DELETE}
	
	private final ChangeType type;
	
	private final Object model;
	
    public ModelChangedEvent(final Object model, final ChangeType type) {
        super(CommonConstants.Events.CHANGE_MODEL);
        
        this.model = model;
        this.type = type;
    }

	public ChangeType getType() {
		return type;
	}

	public Object getModel() {
		return model;
	}
}
