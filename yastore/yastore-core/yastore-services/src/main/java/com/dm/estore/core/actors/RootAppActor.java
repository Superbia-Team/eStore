package com.dm.estore.core.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

import com.dm.estore.common.dto.requests.system.CreateChildRequest;
import com.dm.estore.common.dto.requests.system.CreateChildResponse;

public abstract class RootAppActor extends UntypedActor {
    
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    
    protected abstract ActorSystem actorSystem();
    
    @Override
    public void onReceive(Object message) throws Exception {
    	if (message instanceof CreateChildRequest) {
    		CreateChildRequest createRequest = (CreateChildRequest) message;
    		
    		Props props = createRequest.isFromConfig() ? FromConfig.getInstance().props(createRequest.getProps()) : createRequest.getProps();
    		ActorRef child = getContext().actorOf(props, createRequest.getChildContextName());
    		LOG.debug("Dynamic actor created: " + child.path().toString());
    		
    		if (getSender() != null && getSender() != getContext().system().deadLetters()) {
    			getSender().tell(new CreateChildResponse(child, createRequest.getChildId()), getSelf());
    		}
    		
    	} else {
    		unhandled(message);
    	}
    }
}
