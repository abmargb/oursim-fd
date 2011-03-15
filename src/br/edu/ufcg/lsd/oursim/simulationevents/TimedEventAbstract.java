package br.edu.ufcg.lsd.oursim.simulationevents;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * An convenient class to all events that hold some source.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 * @param <T>
 *            The type of the source holded by the event.
 */
public abstract class TimedEventAbstract<T> extends TimedEvent {

	/**
	 * the source holded by the event.
	 */
	protected T source;

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 */
	protected TimedEventAbstract(long time, int priority) {
		super(time, priority);
	}

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param source
	 *            the source holded by the event.
	 */
	protected TimedEventAbstract(long time, T source) {
		super(time);
		this.source = source;
	}

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param source
	 *            the source holded by the event.
	 */
	protected TimedEventAbstract(long time, int priority, T source) {
		super(time, priority);
		this.source = source;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("time", time).append("source", source).toString();
	}

}