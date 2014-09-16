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

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.japi.EventBus;

import com.dm.estore.common.akka.events.BusEvent;
import com.dm.estore.common.constants.CommonConstants;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author dmorozov
 */
@Component(EventActors.CONTEXT_NAME)
@Scope("prototype")
public class EventActors extends UntypedActor {
    
    public static final String CONTEXT_NAME = "events";
    
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    
    @Resource
    private EventBus<BusEvent<?>, ActorRef, String> eventBus;
    
    private ActorRef logConfigurationActor;
    
    @Override
    public void preStart() {        
        logConfigurationActor = getContext().actorOf(Props.create(LogConfigurationReloadActor.class), LogConfigurationReloadActor.CONTEXT_NAME);
        getContext().watch(logConfigurationActor);
        
        eventBus.subscribe(logConfigurationActor, CommonConstants.Events.CHANGE_CONFIGURATION);
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
    	LOG.debug("Received event message");
        if (message instanceof Terminated) {
            final Terminated t = (Terminated) message;
            if (t.getActor().equals(logConfigurationActor)) {
                eventBus.unsubscribe(logConfigurationActor);
                getContext().stop(getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
