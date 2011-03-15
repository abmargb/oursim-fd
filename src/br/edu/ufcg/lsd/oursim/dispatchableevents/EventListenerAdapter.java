package br.edu.ufcg.lsd.oursim.dispatchableevents;

/**
 * 
 * An adapter for the listeners of the events that happen to some source. This
 * is just a tagging interface and is here just for state its importance.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * @see {@link EventListener}
 */
public abstract class EventListenerAdapter implements EventListener {

	@Override
	public int compareTo(EventListener o) {
		return this.hashCode() - o.hashCode();
	}

}
