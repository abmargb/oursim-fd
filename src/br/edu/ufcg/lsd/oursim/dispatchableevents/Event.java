package br.edu.ufcg.lsd.oursim.dispatchableevents;

import java.util.EventObject;

/**
 * 
 * The class from which all event state objects shall be derived.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @param <T>
 *            The type of the object on which the Event initially occurred.
 * 
 */
public class Event<T> extends EventObject {

	private static final long serialVersionUID = 4966574677524224231L;

	/**
	 * The instant at which the event have occurred.
	 */
	private Long time = null;

	/**
	 * Constructs a prototypical Event.
	 * 
	 * @param source
	 *            The object on which the Event initially occurred.
	 * @exception IllegalArgumentException
	 *                if source is null.
	 */
	public Event(T source) {
		super(source);
	}

	public Event(long time, T source) {
		this(source);
		this.time = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#getSource()
	 */
	@SuppressWarnings("unchecked")
	public final T getSource() {
		return (T) source;
	}

	/**
	 * Gets the instant at which this event have occurred.
	 * 
	 * @return The instant at which this event have occurred.
	 */
	public final long getTime() {
		assert time != null;
		return time;
	}

}
