package br.edu.ufcg.lsd.oursim.dispatchableevents;

/**
 * 
 * The filter that determines which events the listener wants to be notified.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @param <T>
 *            The type of the Event to be filted.
 */
public interface EventFilter<T extends Event<?>> {

	/**
	 * A lenient EventFilter that accepts all events.
	 */
	EventFilter<Event<?>> ACCEPT_ALL = new EventFilter<Event<?>>() {

		@Override
		public boolean accept(Event<?> event) {
			return true;
		}

	};

	/**
	 * Tests whether or not the specified event should be accepted by a
	 * listener.
	 * 
	 * @param event
	 *            The event to be tested
	 * @return <code>true</code> if and only if <code>event</code> should be
	 *         accepted.
	 */
	boolean accept(T event);

}
