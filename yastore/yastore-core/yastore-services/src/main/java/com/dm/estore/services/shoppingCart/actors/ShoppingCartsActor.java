package com.dm.estore.services.shoppingCart.actors;

import static com.dm.estore.core.services.config.akka.AkkaSpringExtension.SpringExtProvider;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;

import com.dm.estore.common.utils.OrderUtils;
import com.dm.estore.common.dto.requests.shoppingCart.UpdateShoppingCartRequest;

@Component(value = ShoppingCartsActor.CONTEXT_NAME)
@Scope("prototype")
public class ShoppingCartsActor extends UntypedActor {
	
	public static final String CONTEXT_NAME = "shoppingCarts";
	public static final String ACTOR_NAME = CONTEXT_NAME;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UpdateShoppingCartRequest) {
			UpdateShoppingCartRequest request = (UpdateShoppingCartRequest) message;
		
			ActorRef actorRef = null;
			if (!StringUtils.isEmpty(request.getCode())) {
				final ActorRef child = getContext().getChild(request.getCode());
				if (child != null && child != ActorRef.noSender() && child != getContext().system().deadLetters()) {
					actorRef = child;
				}
			}
			
			if (null == actorRef) {
				request = new UpdateShoppingCartRequest(OrderUtils.generateOrderCode(), request.getUpdates());
				Props actorProps = SpringExtProvider.get(getContext().system()).props(ShoppingCartActor.CONTEXT_NAME);
				actorRef = getContext().actorOf(FromConfig.getInstance().props(actorProps), request.getCode());
			}
			actorRef.tell(request, getSender());
				
		} else {
			unhandled(message);
		}
		
		
	}
}
