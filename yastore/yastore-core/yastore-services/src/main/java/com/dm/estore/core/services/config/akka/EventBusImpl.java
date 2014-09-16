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
package com.dm.estore.core.services.config.akka;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;

import com.dm.estore.common.akka.events.BusEvent;

/**
 *
 * @author dmorozov
 */
public class EventBusImpl extends LookupEventBus<BusEvent<?>, ActorRef, String> {
    
    private static final int INITIAL_CLASIFIERS_NUMBER = 5;

    /**
     * Initial size of the index data structure used internally (i.e. the
     * expected number of different classifiers)
     *
     * @return Initial bus classifiers number
     */
    @Override
    public int mapSize() {
        return INITIAL_CLASIFIERS_NUMBER;
    }

    /**
     * Used to define a partial ordering of subscribers. The ordering is based
     * on Event.channel
     *
     * @param subscriberA The first subscriber
     * @param subscriberB The second subscriber
     * @return Comparison result
     */
    @Override
    public int compareSubscribers(ActorRef subscriberA, ActorRef subscriberB) {
        return subscriberA.compareTo(subscriberB);
    }

    /**
     * Extract the classification data from the event.
     *
     * @param event {@link Event} to classify
     * @return Channel string from the {@link Event}
     */
    @Override
    public String classify(BusEvent<?> event) {
        return event.getTopic();
    }

    @Override
    public void publish(BusEvent<?> event, ActorRef subscriber) {
        subscriber.tell(event, ActorRef.noSender());
    }
}
