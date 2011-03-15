package br.edu.ufcg.lsd.oursim.simulationevents;

/**
 * 
 * The root class to all events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @author Matheus G. do Rêgo, matheusgr@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public abstract class TimedEvent implements Comparable<TimedEvent> {

	/**
	 * flag that indicates if the event has been cancelled, that is, mustn't
	 * been processed anymore.
	 */
	private boolean cancel;

	/**
	 * the time at which the event have occurred.
	 */
	protected long time;

	// quanto menor, maior a preferência para ser executado antes
	// range -> [1-10]
	private int priority;

	/**
	 * An ordinary event with a default priority.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 */
	protected TimedEvent(long time) {
		this(time, 5);
	}

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 */
	protected TimedEvent(long time, int priority) {
		this.time = time;
		this.priority = priority;
		this.cancel = false;
	}

	/**
	 * The method that must be overrided by the concret classes that extends
	 * this root class.
	 */
	protected abstract void doAction();

	/**
	 * A textual representation of the event's type.
	 * 
	 * @return the textual representation of the event's type.
	 */
	public String getType() {
		String thisClassSimpleName = this.getClass().getSimpleName();
		// TODO: following an implicit name convention
		String eventName = thisClassSimpleName.substring(0, thisClassSimpleName.indexOf("Event"));
		return eventName;
	}

	/**
	 * to cancell this event.
	 */
	public void cancel() {
		this.cancel = true;
	}

	/**
	 * Performs the action of this event.
	 */
	public final void action() {
		if (!cancel) {
			doAction();
		}
	}

	/**
	 * checks if this event have been cancelled.
	 * 
	 * @return <code>true</code> if this event have been cancelled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCancelled() {
		return this.cancel;
	}

	/**
	 * @return the time at which the event have occurred.
	 */
	public long getTime() {
		return this.time;
	}

	@Override
	public int compareTo(TimedEvent ev) {
		long diffTime = this.time - ev.time;
		// eventos no mesmo tempo?
		if (diffTime == 0) {
			// eventos de prioridades diferentes?
			if (this.priority != ev.priority) {
				return (this.priority > ev.priority) ? 1 : -1;
			} else { // eventos de mesma prioridade
				return 0;
			}
		} else { // eventos em tempos diferentes
			return (diffTime > 0) ? 2 : -2;
		}
	}

}