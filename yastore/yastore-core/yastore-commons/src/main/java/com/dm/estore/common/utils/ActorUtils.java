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

package com.dm.estore.common.utils;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.UntypedActorContext;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.AskableActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.dm.estore.common.ExecutionCallback;
import com.dm.estore.common.akka.ServicesConstants;
import com.dm.estore.common.constants.CommonConstants;

/**
 *
 * @author dmorozov
 */
public class ActorUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(ActorUtils.class);
    
    public static final String ROOT_CONTEXT_PATH = "/user/";
    public static final String PATH_SEPARATOR = "/";
    public static final String ROOT_SELECTOR = "akka://" + CommonConstants.App.CFG_APP_NAME + "/user/";
    
    public static String translatePath(String parentPath, String actorPath) {
        return ROOT_CONTEXT_PATH + parentPath + PATH_SEPARATOR + actorPath;
    }
    
    public static ActorRef findRootActor(final ActorSystem actorSystem, final String rootActorName) {
        try {

            //ActorSelection sel = actorSystem.actorSelection(ROOT_SELECTOR + rootActorName);
        	ActorSelection sel = actorSystem.actorSelection(ROOT_CONTEXT_PATH + rootActorName);
            
            Timeout t = new Timeout(5, TimeUnit.SECONDS);
            AskableActorSelection asker = new AskableActorSelection(sel);
            Future<Object> fut = asker.ask(new Identify(1), t);
            ActorIdentity ident = (ActorIdentity) Await.result(fut, t.duration());
            ActorRef ref = ident.getRef();
            return ref != null && ref != ActorRef.noSender() && ref != actorSystem.deadLetters() ? ref : null;
        } catch (Exception ex) {
            LOG.error("Unable to retrieve parent actor", ex);
        }
        
        return null;
    }
    
	public static ActorRef findActor(final UntypedActorContext context, final String path) {
		try {
			ActorSelection sel = context.actorSelection(path);

			Timeout t = new Timeout(1, TimeUnit.SECONDS);
			AskableActorSelection asker = new AskableActorSelection(sel);
			Future<Object> fut = asker.ask(new Identify(1), t);
			ActorIdentity ident = (ActorIdentity) Await.result(fut, t.duration());
			ActorRef ref = ident.getRef();
            return ref != null && ref != ActorRef.noSender() && ref != context.system().deadLetters() ? ref : null;
		} catch (Exception e) {
			LOG.info("Actor for path not found: " + path);
		}

		return null;
	}
	
	private static final class ProcessResult<T> extends OnSuccess<T> {
	    
	    final ExecutionCallback<T> callback;
	    
	    public ProcessResult(final ExecutionCallback<T> callback) {
	        this.callback = callback;
	    }
	    
	    @Override public final void onSuccess(T t) {
	        callback.done(t);
	        callback.complete(true, t, null);
	    }
	}
	
	private static final class ProcessFailure extends OnFailure {
	    final ExecutionCallback<?> callback;
        
        public ProcessFailure(final ExecutionCallback<?> callback) {
            this.callback = callback;
        }
        @Override
        public void onFailure(Throwable failure) throws Throwable {
            callback.failed(failure);
            callback.complete(false, null, failure);
        }
    }
	
	public static <REQUEST, RESPONSE> void callAsyncService(final String serviceName, final REQUEST request, final ExecutionCallback<RESPONSE> callback) {
	    ActorSystem actorSystem = com.dm.estore.common.config.Cfg.instance().actorSystem();
	    String actorPath = ActorUtils.translatePath(ServicesConstants.SERVICES_ROOT_PATH, serviceName);
        ActorSelection selection = actorSystem.actorSelection(actorPath);
        
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        @SuppressWarnings("unchecked")
        Future<RESPONSE> future = (Future<RESPONSE>) Patterns.ask(selection, request, timeout);
        future.onSuccess(new ProcessResult<RESPONSE>(callback), actorSystem.dispatcher());
        future.onFailure(new ProcessFailure(callback), actorSystem.dispatcher());
	}
	
	/**
	 * Example:
	 * ActorUtils.pipeProcessing(targetActor, new Mapper<Iterable<Object>, T>() {
              public T apply(Iterable<Object> coll) {
                final Iterator<Object> it = coll.iterator();
                final String x = (String) it.next();
                final String s = (String) it.next();
                return new T(x, s);
              }
            }, 
            
            actors1, actor2, actor3);
	 * 
	 * @param targetActor Target actor to get all results
	 * @param mapper Mapper to combine all results together
	 * @param actors List of actors to execute
	 */
	public static <REQUEST, RESPONSE> void pipeProcessing(REQUEST request, ActorRef targetActor, Mapper<Iterable<Object>, RESPONSE> mapper, ActorRef ... actors) {
        final Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        
        final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        for (ActorRef actor : actors) {
            futures.add(ask(actor, "another request", t));
        }

        ActorSystem system = com.dm.estore.common.config.Cfg.instance().actorSystem();
        final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
            system.dispatcher());
         
        final Future<RESPONSE> transformed = aggregate.map(mapper, system.dispatcher());
        pipe(transformed, system.dispatcher()).to(targetActor);
	}	
}
