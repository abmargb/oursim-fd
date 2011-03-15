package br.edu.ufcg.lsd.oursim.dispatchableevents;

/**
 * 
 * The listener interface for receiving events. This is just an tagging
 * interface and is here just for state its importance. The class that is
 * interested in processing the event either implements the appropriate
 * subinterface of this (and all the methods it contains) or extends the
 * respective abstract subclass of {@link EventListenerAdapter} class
 * (overriding only the methods of interest). The listener object created from
 * that class is then registered with a respective subclass of
 * {@link EventDispatcher} using the dispatcher's
 * {@link EventDispatcher#addListener(EventListener)} method. When the source's
 * status changes by virtue of any type of event, the relevant method in the
 * listener object is invoked, and the appropriate Event is passed to it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface EventListener extends java.util.EventListener, Comparable<EventListener> {

}
