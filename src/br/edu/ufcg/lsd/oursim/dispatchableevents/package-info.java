/**
 * Contains the class referents to events that are dispatchable to interested listener. 
 * The class in this packages follows the Observer Design Pattern to decouple the caller
 * from the callee of a message.
 * 
 * Each family of class that implement the pattern are located in its own package. 
 * Each package must contain at least three classes, from the types :
 * 
 * <ol>
 * 	<li>{@link EventFilter}
 * 	<li>{@link EventListener}
 * 	<li>{@link EventDispatcher}
 * </ol>
 * 
 * Apart from this three types, it could contain an {@link EventListenerAdapter} as a
 * default (usually empty) implementation of the listener.
 */
package br.edu.ufcg.lsd.oursim.dispatchableevents;