package br.edu.ufcg.lsd.oursim.dispatchableevents;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;

/**
 * 
 * A dispatcher for the {@link Event}s that ocurrs in some source.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @param <S>
 *            The type of the source of the event to be dispatched.
 * @param <L>
 *            The type of the listeners of the event to be dispatched.
 * @param <F>
 *            The type of the filter used by listeners of the event to be
 *            dispatched.
 */
public abstract class EventDispatcher<S, L extends EventListener, F extends EventFilter<?>> {

	/**
	 * The collection of the listeners of the events.
	 */
	private final Set<L> listeners;

	/**
	 * The mapping between a listener and its filter.
	 */
	private final Map<L, F> listenerToFilter;

	/**
	 * An ordinary constructor.
	 */
	protected EventDispatcher() {
		this.listeners = new TreeSet<L>();
		this.listenerToFilter = new TreeMap<L, F>();
	}

	/**
	 * Adds a listener to this dispatcher.
	 * 
	 * @param listener
	 *            The listener to be added.
	 * @param filter
	 *            The filter that determines which events the listener wants to
	 *            be notified.
	 */
	public final void addListener(L listener, F filter) {
		addListener(listener);
		F oldValue = this.listenerToFilter.put(listener, filter);
		assert oldValue != null;
	}

	/**
	 * An convenient method to adds a listener that accepts all events
	 * dispatched.
	 * 
	 * @param listener
	 *            The listener to be added.
	 */
	public abstract void addListener(L listener);

	/**
	 * Removes a listener from this dispatcher.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 * @return <code>true</code> if the listener has been succesfully removed,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean removeListener(L listener);

	public void clear() {
		this.getListeners().clear();
	}

	/**
	 * Performs a dispatching of the given event to all the listener that accept
	 * it in this dispatcher.
	 * 
	 * @param type
	 *            the type of the event. This type must be one of the valid
	 *            types in each dispatcher. See one of the class to check how it
	 *            works ({@link JobEventDispatcher#dispatch(Enum, Event)}).
	 * @param event
	 *            the event to be dispatched.
	 */
	@SuppressWarnings("unchecked")
	protected abstract void dispatch(Enum type, Event<S> event);

	protected Set<L> getListeners() {
		return listeners;
	}

	protected Map<L, F> getListenerToFilter() {
		return listenerToFilter;
	}

}
