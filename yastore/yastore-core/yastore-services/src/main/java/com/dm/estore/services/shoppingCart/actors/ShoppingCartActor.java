package com.dm.estore.services.shoppingCart.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dm.estore.common.dto.requests.shoppingCart.UpdateShoppingCartRequest;

import akka.actor.UntypedActor;

@Component(value = ShoppingCartActor.CONTEXT_NAME)
@Scope("prototype")
public class ShoppingCartActor extends UntypedActor {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartActor.class);
	
	public static final String CONTEXT_NAME = "shoppingCart";

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UpdateShoppingCartRequest) {
			UpdateShoppingCartRequest request = (UpdateShoppingCartRequest) message;
			// TODO: implement shopping cart update
			
			LOG.debug("Udate shopping cart: " + request.getCode());
			
		} else {
			unhandled(message);
		}
	}

}
