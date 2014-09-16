package com.dm.estore.common.dto.requests.system;

import akka.actor.Props;

public class CreateChildRequest {

	private final Props props;
	private final String childId;
	private final String childContextName;
	private final boolean fromConfig;

	public CreateChildRequest(final Props props, final String childId, final String childContext, boolean fromConfig) {
		this.props = props;
		this.childContextName = childContext;
		this.childId = childId;
		this.fromConfig = fromConfig;
	}

	public CreateChildRequest(final Props props, final String childId, final String childContext) {
		this.props = props;
		this.childContextName = childContext;
		this.childId = childId;
		this.fromConfig = true;
	}
	
	public Props getProps() {
		return props;
	}

	public String getChildContextName() {
		return childContextName;
	}

	public String getChildId() {
		return childId;
	}

	public boolean isFromConfig() {
		return fromConfig;
	}
}
