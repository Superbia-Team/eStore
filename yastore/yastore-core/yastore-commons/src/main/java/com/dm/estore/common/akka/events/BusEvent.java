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

/**
 *
 * @author dmorozov
 * @param <T> Message payload type
 */
public class BusEvent<T> {

    private final String topic;
    private final T payload;

    public BusEvent(final String topic) {
        this.topic = topic;
        this.payload = null;
    }
    
    public BusEvent(final String topic, final T payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }
    
    public T getPayload() {
        return payload;
    }
}
