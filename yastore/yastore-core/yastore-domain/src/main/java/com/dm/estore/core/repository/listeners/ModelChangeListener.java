package com.dm.estore.core.repository.listeners;

import javax.annotation.Resource;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Configurable;

import akka.actor.ActorRef;
import akka.event.japi.EventBus;

import com.dm.estore.common.akka.events.BusEvent;
import com.dm.estore.common.akka.events.ModelChangedEvent;
import com.dm.estore.common.spring.AutowireHelper;

@Configurable
public class ModelChangeListener {

	@Resource
	private EventBus<BusEvent<?>, ActorRef, String> eventBus;

	@PostPersist
	public void postPersist(Object model) {
		AutowireHelper.autowire(this, this.eventBus);
		if (eventBus != null) {
			eventBus.publish(new ModelChangedEvent(model, ModelChangedEvent.ChangeType.CREATE));
		}
	}

	@PostRemove
	public void postRemove(Object model) {
		AutowireHelper.autowire(this, this.eventBus);
		if (eventBus != null) {
			eventBus.publish(new ModelChangedEvent(model, ModelChangedEvent.ChangeType.DELETE));
		}
	}

	@PostUpdate
	public void postUpdate(Object model) {
		AutowireHelper.autowire(this, this.eventBus);
		if (eventBus != null) {
			eventBus.publish(new ModelChangedEvent(model, ModelChangedEvent.ChangeType.UPDATE));
		}
	}
}
