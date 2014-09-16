package com.dm.estore.search.akka;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static com.dm.estore.core.services.config.akka.AkkaSpringExtension.SpringExtProvider;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.japi.EventBus;
import akka.japi.Function;
import akka.routing.FromConfig;

import com.dm.estore.common.akka.events.BusEvent;
import com.dm.estore.common.akka.events.ModelChangedEvent;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.exceptions.AbstractBusinessException;
import com.dm.estore.common.exceptions.AbstractRuntimeException;

/**
* Parent indexing actor
* 
* @author dmorozov
*/
@Component(SearchIndexingActor.CONTEXT_NAME)
@Scope("prototype")
public class SearchIndexingActor extends UntypedActor {
	
    public static final String CONTEXT_NAME = "searchIndexingActor";
    public static final String ACTOR_NAME = SearchIndexingActor.CONTEXT_NAME;
    
	private static final Logger LOG = LoggerFactory.getLogger(SearchIndexingActor.class);

    @Resource
	private EventBus<BusEvent<?>, ActorRef, String> eventBus;
    
	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
			Duration.create("1 minute"), new Function<Throwable, Directive>() {
				@Override
				public Directive apply(Throwable t) {
					if (t instanceof AbstractBusinessException) {
						return resume();
					} else if (t instanceof AbstractRuntimeException) {
						return escalate();						
					} else {
						return restart();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

    @Override
    public void preStart() throws Exception {
    	super.preStart();
    	
    	Props actorProps = SpringExtProvider.get(getContext().system()).props(IndexingWorkerActor.CONTEXT_NAME);
		getContext().actorOf(FromConfig.getInstance().props(actorProps), IndexingWorkerActor.ACTOR_NAME);
		
    	eventBus.subscribe(getSelf(), CommonConstants.Events.CHANGE_MODEL);
    }
    
    @Override
    public void postStop() throws Exception {
    	eventBus.unsubscribe(getSelf());
    	super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ModelChangedEvent) {
        	LOG.debug("Indexing parent actor. Thread: [{}]", Thread.currentThread().getName());
        	ActorSelection worker = getContext().actorSelection(IndexingWorkerActor.ACTOR_NAME);
        	worker.tell(message, getSender());
        } else {
            unhandled(message);
        }
    }
}
