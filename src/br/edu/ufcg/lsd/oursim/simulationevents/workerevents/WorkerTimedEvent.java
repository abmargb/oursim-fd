package br.edu.ufcg.lsd.oursim.simulationevents.workerevents;

import br.edu.ufcg.lsd.oursim.simulationevents.TimedEventAbstract;

/**
 * 
 * The root class of all worker related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public abstract class WorkerTimedEvent extends TimedEventAbstract<String> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param machineName
	 *            the name of the machine this event relates to.
	 */
	public WorkerTimedEvent(long time, int priority, String machineName) {
		super(time, priority, machineName);
	}

}
