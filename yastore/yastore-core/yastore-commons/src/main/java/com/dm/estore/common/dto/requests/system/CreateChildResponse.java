package com.dm.estore.common.dto.requests.system;

import akka.actor.ActorRef;

public class CreateChildResponse {
	
	private final ActorRef child;
	private final String childId;

	public String getChildId() {
		return childId;
	}

	public CreateChildResponse(final ActorRef child, final String childId) {
		this.child = child;
		this.childId = childId;
	}

	public ActorRef getChild() {
		return child;
	}
}
