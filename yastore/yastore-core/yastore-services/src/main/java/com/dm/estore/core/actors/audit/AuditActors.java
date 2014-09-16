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

package com.dm.estore.core.actors.audit;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author dmorozov
 */
@Component(AuditActors.CONTEXT_NAME)
@Scope("prototype")
public class AuditActors extends UntypedActor {
    
    public static final String CONTEXT_NAME = "auditActors";
    
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    @Resource(name = "actorSystem")
    private ActorSystem actorSystem;
    
    @Override
    public void onReceive(Object message) throws Exception {
    	LOG.debug("Received audit message");
        unhandled(message);
    }
}
